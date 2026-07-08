package com.hotel.servlet.admin;

import com.hotel.dao.RoomDao;
import com.hotel.dao.RoomTypeDao;
import com.hotel.model.Room;
import com.hotel.service.BookingException;
import com.hotel.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** 后台房间管理：增删改 + 房态流转（含"已清洁"） */
@WebServlet("/admin/rooms")
public class AdminRoomServlet extends HttpServlet {

    private final RoomDao roomDao = new RoomDao();
    private final RoomTypeDao roomTypeDao = new RoomTypeDao();
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equals(action)) {
            req.setAttribute("types", roomTypeDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(req, resp);
        } else if ("edit".equals(action)) {
            req.setAttribute("room", roomDao.findById(parseInt(req.getParameter("id"), 0)));
            req.setAttribute("types", roomTypeDao.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(req, resp);
        } else {
            Integer typeId = parseIntOrNull(req.getParameter("typeId"));
            String status = req.getParameter("status");
            req.setAttribute("rooms", roomDao.findAll(typeId, status));
            req.setAttribute("types", roomTypeDao.findAll());
            req.setAttribute("filterType", typeId);
            req.setAttribute("filterStatus", status);
            passFlash(req);
            req.getRequestDispatcher("/WEB-INF/views/admin/rooms.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession();
        try {
            if ("status".equals(action)) {
                roomService.changeStatus(parseInt(req.getParameter("id"), 0), req.getParameter("target"));
                session.setAttribute("flash", "房态已更新");
            } else if ("delete".equals(action)) {
                roomService.delete(parseInt(req.getParameter("id"), 0));
                session.setAttribute("flash", "房间已删除");
            } else {
                Room r = new Room();
                int id = parseInt(req.getParameter("id"), 0);
                r.setId(id);
                r.setRoomNo(req.getParameter("roomNo"));
                r.setTypeId(parseInt(req.getParameter("typeId"), 0));
                r.setFloor(parseInt(req.getParameter("floor"), 1));
                r.setStatus(req.getParameter("status"));
                r.setNote(req.getParameter("note"));
                if (id > 0) {
                    roomService.update(r);
                    session.setAttribute("flash", "房间已更新");
                } else {
                    roomService.add(r);
                    session.setAttribute("flash", "房间已新增");
                }
            }
        } catch (BookingException e) {
            session.setAttribute("flash", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/rooms");
    }

    private void passFlash(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Object flash = session.getAttribute("flash");
        if (flash != null) {
            req.setAttribute("flash", flash);
            session.removeAttribute("flash");
        }
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private Integer parseIntOrNull(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}
