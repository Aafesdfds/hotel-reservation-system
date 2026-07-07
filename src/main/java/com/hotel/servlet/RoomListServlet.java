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
import java.util.List;
import java.util.Map;

/** 房型列表 + 按入住日期查空房 */
@WebServlet("/rooms")
public class RoomListServlet extends HttpServlet {

    private final RoomTypeDao roomTypeDao = new RoomTypeDao();
    private final RoomDao roomDao = new RoomDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LocalDate today = LocalDate.now();
        LocalDate checkin = parseDate(req.getParameter("checkin"), today);
        LocalDate checkout = parseDate(req.getParameter("checkout"), today.plusDays(1));
        if (!checkout.isAfter(checkin)) {
            checkout = checkin.plusDays(1);
        }

        List<RoomType> types = roomTypeDao.findAll();
        Map<Integer, Integer> avail = roomDao.availableCountByType(Date.valueOf(checkin), Date.valueOf(checkout));
        for (RoomType t : types) {
            t.setAvailableCount(avail.getOrDefault(t.getId(), 0));
        }

        req.setAttribute("types", types);
        req.setAttribute("checkin", checkin.toString());
        req.setAttribute("checkout", checkout.toString());
        req.setAttribute("nights", checkout.toEpochDay() - checkin.toEpochDay());
        req.getRequestDispatcher("/WEB-INF/views/user/rooms.jsp").forward(req, resp);
    }

    static LocalDate parseDate(String s, LocalDate def) {
        if (s == null || s.trim().isEmpty()) {
            return def;
        }
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
