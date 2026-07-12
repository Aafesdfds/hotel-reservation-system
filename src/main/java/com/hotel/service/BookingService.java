package com.hotel.service;

import com.hotel.dao.ReservationDao;
import com.hotel.dao.RoomDao;
import com.hotel.dao.RoomTypeDao;
import com.hotel.model.Reservation;
import com.hotel.model.RoomType;
import com.hotel.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 预订核心逻辑。重点是并发处理：
 * 多个人同时预订同一房型时，用数据库事务 + 行锁把同房型的下单串行化，
 * 保证有几间空房就只能成交几单，绝不会把同一间房卖给两个人。
 */
public class BookingService {

    private final RoomDao roomDao = new RoomDao();
    private final RoomTypeDao roomTypeDao = new RoomTypeDao();
    private final ReservationDao reservationDao = new ReservationDao();

    private static final AtomicInteger SEQ = new AtomicInteger(0);
    private static final DateTimeFormatter ORDER_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public Reservation book(int userId, int typeId, LocalDate checkin, LocalDate checkout,
                            String guestName, String guestPhone, String guestIdCard,
                            int guestCount, String remark) throws BookingException {
        // 基本校验
        if (checkin == null || checkout == null) {
            throw new BookingException("请选择入住和退房日期");
        }
        if (!checkout.isAfter(checkin)) {
            throw new BookingException("退房日期必须晚于入住日期");
        }
        if (checkin.isBefore(LocalDate.now())) {
            throw new BookingException("入住日期不能早于今天");
        }
        RoomType type = roomTypeDao.findById(typeId);
        if (type == null) {
            throw new BookingException("房型不存在");
        }
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new BookingException("请填写入住人姓名");
        }
        if (guestPhone == null || !guestPhone.matches("1\\d{10}")) {
            throw new BookingException("请填写正确的手机号");
        }
        if (guestCount < 1 || guestCount > type.getCapacity()) {
            throw new BookingException("入住人数需在 1 到 " + type.getCapacity() + " 人之间");
        }

        int nights = (int) ChronoUnit.DAYS.between(checkin, checkout);
        BigDecimal total = type.getPrice().multiply(BigDecimal.valueOf(nights));
        Date sqlCheckin = Date.valueOf(checkin);
        Date sqlCheckout = Date.valueOf(checkout);

        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false);

            // 第 1 步：锁住该房型的所有房间行，让同房型的并发下单在此排队
            roomDao.lockRoomsOfType(c, typeId);

            // 第 2 步：在锁的保护下找一间该日期空闲的房
            int roomId = roomDao.findFreeRoom(c, typeId, sqlCheckin, sqlCheckout);
            if (roomId < 0) {
                c.rollback();
                throw new BookingException("很抱歉，该房型在所选日期已订满，请换个日期或选其它房型");
            }

            // 第 3 步：落单，房间分配好
            Reservation r = new Reservation();
            r.setOrderNo(generateOrderNo());
            r.setUserId(userId);
            r.setTypeId(typeId);
            r.setRoomId(roomId);
            r.setCheckinDate(sqlCheckin);
            r.setCheckoutDate(sqlCheckout);
            r.setNights(nights);
            r.setGuestName(guestName.trim());
            r.setGuestPhone(guestPhone.trim());
            r.setGuestIdCard(guestIdCard == null ? null : guestIdCard.trim());
            r.setGuestCount(guestCount);
            r.setTotalPrice(total);
            r.setStatus("RESERVED");
            r.setRemark(remark);
            int id = reservationDao.insert(c, r);
            r.setId(id);

            c.commit();
            return r;
        } catch (SQLException e) {
            rollback(c);
            throw new BookingException("下单失败，请稍后重试");
        } finally {
            restore(c);
        }
    }

    /** 用户取消自己的订单，仅限已预订、未入住的 */
    public void cancel(int reservationId, int userId) throws BookingException {
        Reservation r = reservationDao.findById(reservationId);
        if (r == null || r.getUserId() != userId) {
            throw new BookingException("订单不存在");
        }
        if (!"RESERVED".equals(r.getStatus())) {
            throw new BookingException("只有已预订、未入住的订单可以取消");
        }
        reservationDao.updateStatus(reservationId, "CANCELLED");
    }

    private String generateOrderNo() {
        int n = SEQ.incrementAndGet() % 1000;
        return "HB" + LocalDate.now().atStartOfDay()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSS"))
                + String.format("%03d", n);
    }

    private void rollback(Connection c) {
        if (c != null) {
            try {
                c.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void restore(Connection c) {
        if (c != null) {
            try {
                c.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            DBUtil.close(c);
        }
    }
}
