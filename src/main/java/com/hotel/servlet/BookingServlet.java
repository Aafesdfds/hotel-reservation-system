package com.hotel.servlet;

import com.hotel.dao.RoomDao;
import com.hotel.dao.RoomTypeDao;
import com.hotel.model.Reservation;
import com.hotel.model.RoomType;
import com.hotel.model.User;
import com.hotel.service.BookingException;
import com.hotel.service.BookingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/** 提交预订 */
@WebServlet("/booking")
public class BookingServlet extends HttpServlet {

    private final RoomTypeDao roomTypeDao = new RoomTypeDao();
    private final RoomDao roomDao = new RoomDao();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int typeId = parseInt(req.getParameter("typeId"), 0);
        RoomType type = roomTypeDao.findById(typeId);
        if (type == null) {
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate checkin = RoomListServlet.parseDate(req.getParameter("checkin"), today);
        LocalDate checkout = RoomListServlet.parseDate(req.getParameter("checkout"), today.plusDays(1));
        if (!checkout.isAfter(checkin)) {
            checkout = checkin.plusDays(1);
        }
        showForm(req, resp, type, checkin.toString(), checkout.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");

        int typeId = parseInt(req.getParameter("typeId"), 0);
        RoomType type = roomTypeDao.findById(typeId);
        String checkinStr = req.getParameter("checkin");
        String checkoutStr = req.getParameter("checkout");
        String guestName = req.getParameter("guestName");
        String guestPhone = req.getParameter("guestPhone");
        String guestIdCard = req.getParameter("guestIdCard");
        int guestCount = parseInt(req.getParameter("guestCount"), 1);
        String remark = req.getParameter("remark");

        try {
            LocalDate checkin = LocalDate.parse(checkinStr);
            LocalDate checkout = LocalDate.parse(checkoutStr);
            Reservation r = bookingService.book(user.getId(), typeId, checkin, checkout,
                    guestName, guestPhone, guestIdCard, guestCount, remark);
            session.setAttribute("flash", "预订成功！订单号 " + r.getOrderNo()
                    + "，已为您分配房间。请到「我的订单」查看。");
            resp.sendRedirect(req.getContextPath() + "/orders");
        } catch (BookingException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("guestName", guestName);
            req.setAttribute("guestPhone", guestPhone);
            req.setAttribute("guestIdCard", guestIdCard);
            req.setAttribute("guestCount", guestCount);
            req.setAttribute("remark", remark);
            showForm(req, resp, type, checkinStr, checkoutStr);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp,
                          RoomType type, String checkin, String checkout)
            throws ServletException, IOException {
        long nights = LocalDate.parse(checkout).toEpochDay() - LocalDate.parse(checkin).toEpochDay();
        if (nights < 1) {
            nights = 1;
        }
        int avail = roomDao.availableCountByType(Date.valueOf(checkin), Date.valueOf(checkout))
                .getOrDefault(type.getId(), 0);
        type.setAvailableCount(avail);
        req.setAttribute("type", type);
        req.setAttribute("checkin", checkin);
        req.setAttribute("checkout", checkout);
        req.setAttribute("nights", nights);
        req.setAttribute("totalPrice", type.getPrice().multiply(BigDecimal.valueOf(nights)));
        req.getRequestDispatcher("/WEB-INF/views/user/booking.jsp").forward(req, resp);
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
