package com.hotel.servlet.admin;

import com.hotel.dao.ReservationDao;
import com.hotel.model.Reservation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** 后台订单管理：查询、办理入住、办理退房、取消 */
@WebServlet("/admin/reservations")
public class AdminReservationServlet extends HttpServlet {

    private final ReservationDao reservationDao = new ReservationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String status = req.getParameter("status");
        String keyword = req.getParameter("keyword");
        req.setAttribute("orders", reservationDao.findAll(status, keyword));
        req.setAttribute("filterStatus", status);
        req.setAttribute("keyword", keyword);
        passFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/reservations.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        int id = parseInt(req.getParameter("id"));
        HttpSession session = req.getSession();
        Reservation r = reservationDao.findById(id);
        if (r == null) {
            session.setAttribute("flash", "订单不存在");
            resp.sendRedirect(req.getContextPath() + "/admin/reservations");
            return;
        }
        if ("checkin".equals(action)) {
            if ("RESERVED".equals(r.getStatus())) {
                reservationDao.checkIn(id);
                session.setAttribute("flash", "已办理入住，房间 " + r.getRoomNo() + " 转为已入住");
            } else {
                session.setAttribute("flash", "只有已预订的订单可以办理入住");
            }
        } else if ("checkout".equals(action)) {
            if ("CHECKED_IN".equals(r.getStatus())) {
                reservationDao.checkOut(id);
                session.setAttribute("flash", "已办理退房，房间 " + r.getRoomNo() + " 转为待清洁");
            } else {
                session.setAttribute("flash", "只有已入住的订单可以办理退房");
            }
        } else if ("cancel".equals(action)) {
            if ("RESERVED".equals(r.getStatus())) {
                reservationDao.updateStatus(id, "CANCELLED");
                session.setAttribute("flash", "订单已取消");
            } else {
                session.setAttribute("flash", "只有已预订未入住的订单可以取消");
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/reservations");
    }

    private void passFlash(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Object flash = session.getAttribute("flash");
        if (flash != null) {
            req.setAttribute("flash", flash);
            session.removeAttribute("flash");
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
