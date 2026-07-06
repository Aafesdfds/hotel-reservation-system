package com.hotel.servlet;

import com.hotel.dao.RoomDao;
import com.hotel.dao.RoomTypeDao;
import com.hotel.model.RoomType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

/** 房型详情 */
@WebServlet("/room")
public class RoomDetailServlet extends HttpServlet {

    private final RoomTypeDao roomTypeDao = new RoomTypeDao();
    private final RoomDao roomDao = new RoomDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseInt(req.getParameter("id"));
        RoomType type = roomTypeDao.findById(id);
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
        int avail = roomDao.availableCountByType(Date.valueOf(checkin), Date.valueOf(checkout))
                .getOrDefault(id, 0);
        type.setAvailableCount(avail);

        req.setAttribute("type", type);
        req.setAttribute("checkin", checkin.toString());
        req.setAttribute("checkout", checkout.toString());
        req.getRequestDispatcher("/WEB-INF/views/user/room-detail.jsp").forward(req, resp);
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
