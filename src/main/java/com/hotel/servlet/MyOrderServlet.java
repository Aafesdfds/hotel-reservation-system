package com.hotel.servlet;

import com.hotel.dao.ReservationDao;
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

/** 我的订单：查看 + 取消 */
@WebServlet("/orders")
public class MyOrderServlet extends HttpServlet {

    private final ReservationDao reservationDao = new ReservationDao();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        req.setAttribute("orders", reservationDao.findByUser(user.getId()));
        Object flash = session.getAttribute("flash");
        if (flash != null) {
            req.setAttribute("flash", flash);
            session.removeAttribute("flash");
        }
        req.getRequestDispatcher("/WEB-INF/views/user/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");
        if ("cancel".equals(action)) {
            int id = parseInt(req.getParameter("id"));
            try {
                bookingService.cancel(id, user.getId());
                session.setAttribute("flash", "订单已取消");
            } catch (BookingException e) {
                session.setAttribute("flash", e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + "/orders");
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
