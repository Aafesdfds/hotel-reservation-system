package com.hotel.dao;

import com.hotel.model.Reservation;
import com.hotel.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    private static final String BASE_SELECT =
            "SELECT res.*, rt.name AS type_name, rm.room_no AS room_no, u.username AS username "
                    + "FROM reservation res "
                    + "JOIN room_type rt ON res.type_id = rt.id "
                    + "LEFT JOIN room rm ON res.room_id = rm.id "
                    + "JOIN `user` u ON res.user_id = u.id ";

    /** 在事务里插入订单，返回自增 id */
    public int insert(Connection c, Reservation r) throws SQLException {
        String sql = "INSERT INTO reservation(order_no,user_id,type_id,room_id,checkin_date,checkout_date,nights,"
                + "guest_name,guest_phone,guest_id_card,guest_count,total_price,status,remark,created_at) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getOrderNo());
            ps.setInt(2, r.getUserId());
            ps.setInt(3, r.getTypeId());
            if (r.getRoomId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, r.getRoomId());
            }
            ps.setDate(5, r.getCheckinDate());
            ps.setDate(6, r.getCheckoutDate());
            ps.setInt(7, r.getNights());
            ps.setString(8, r.getGuestName());
            ps.setString(9, r.getGuestPhone());
            ps.setString(10, r.getGuestIdCard());
            ps.setInt(11, r.getGuestCount());
            ps.setBigDecimal(12, r.getTotalPrice());
            ps.setString(13, r.getStatus());
            ps.setString(14, r.getRemark());
            ps.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public Reservation findById(int id) {
        String sql = BASE_SELECT + "WHERE res.id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询订单失败", e);
        }
    }

    public List<Reservation> findByUser(int userId) {
        String sql = BASE_SELECT + "WHERE res.user_id = ? ORDER BY res.created_at DESC";
        return query(sql, userId);
    }

    /** 后台订单列表，可按状态和关键字（订单号/入住人/手机号）过滤 */
    public List<Reservation> findAll(String status, String keyword) {
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            sql.append(" AND res.status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (res.order_no LIKE ? OR res.guest_name LIKE ? OR res.guest_phone LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY res.created_at DESC");
        List<Reservation> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询订单列表失败", e);
        }
        return list;
    }

    private List<Reservation> query(String sql, Object... args) {
        List<Reservation> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询订单失败", e);
        }
        return list;
    }

    /** 办理入住：订单转已入住，同时把对应房间置为已入住 */
    public void checkIn(int reservationId) {
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false);
            Integer roomId = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT room_id FROM reservation WHERE id=?")) {
                ps.setInt(1, reservationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roomId = (Integer) rs.getObject("room_id");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE reservation SET status='CHECKED_IN' WHERE id=? AND status='RESERVED'")) {
                ps.setInt(1, reservationId);
                ps.executeUpdate();
            }
            if (roomId != null) {
                try (PreparedStatement ps = c.prepareStatement("UPDATE room SET status='OCCUPIED' WHERE id=?")) {
                    ps.setInt(1, roomId);
                    ps.executeUpdate();
                }
            }
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new RuntimeException("办理入住失败", e);
        } finally {
            restore(c);
        }
    }

    /** 办理退房：订单转已退房，房间转为待清洁 */
    public void checkOut(int reservationId) {
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false);
            Integer roomId = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT room_id FROM reservation WHERE id=?")) {
                ps.setInt(1, reservationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roomId = (Integer) rs.getObject("room_id");
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE reservation SET status='CHECKED_OUT' WHERE id=? AND status='CHECKED_IN'")) {
                ps.setInt(1, reservationId);
                ps.executeUpdate();
            }
            if (roomId != null) {
                try (PreparedStatement ps = c.prepareStatement("UPDATE room SET status='DIRTY' WHERE id=?")) {
                    ps.setInt(1, roomId);
                    ps.executeUpdate();
                }
            }
            c.commit();
        } catch (SQLException e) {
            rollback(c);
            throw new RuntimeException("办理退房失败", e);
        } finally {
            restore(c);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE reservation SET status=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新订单状态失败", e);
        }
    }

    // ---- 仪表盘当日数据 ----

    public int countTodayCheckin(Date today) {
        return countByDateCol("checkin_date", today, "('RESERVED','CHECKED_IN')");
    }

    public int countTodayCheckout(Date today) {
        return countByDateCol("checkout_date", today, "('CHECKED_IN','CHECKED_OUT')");
    }

    private int countByDateCol(String col, Date day, String statusIn) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE " + col + "=? AND status IN " + statusIn;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, day);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计当日数据失败", e);
        }
    }

    public int countInHouse(Date today) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE status='CHECKED_IN' AND checkin_date<=? AND checkout_date>?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, today);
            ps.setDate(2, today);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计在住失败", e);
        }
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM reservation WHERE status=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("统计订单失败", e);
        }
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

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setOrderNo(rs.getString("order_no"));
        r.setUserId(rs.getInt("user_id"));
        r.setTypeId(rs.getInt("type_id"));
        r.setRoomId((Integer) rs.getObject("room_id"));
        r.setCheckinDate(rs.getDate("checkin_date"));
        r.setCheckoutDate(rs.getDate("checkout_date"));
        r.setNights(rs.getInt("nights"));
        r.setGuestName(rs.getString("guest_name"));
        r.setGuestPhone(rs.getString("guest_phone"));
        r.setGuestIdCard(rs.getString("guest_id_card"));
        r.setGuestCount(rs.getInt("guest_count"));
        r.setTotalPrice(rs.getBigDecimal("total_price"));
        r.setStatus(rs.getString("status"));
        r.setRemark(rs.getString("remark"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setTypeName(rs.getString("type_name"));
        r.setRoomNo(rs.getString("room_no"));
        r.setUsername(rs.getString("username"));
        return r;
    }
}
