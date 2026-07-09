package com.hotel.servlet.admin;

import com.hotel.dao.RoomTypeDao;
import com.hotel.model.RoomType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

/** 后台房型管理：增删改查 */
@WebServlet("/admin/types")
public class AdminRoomTypeServlet extends HttpServlet {

    private final RoomTypeDao roomTypeDao = new RoomTypeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("new".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/admin/type-form.jsp").forward(req, resp);
        } else if ("edit".equals(action)) {
            RoomType t = roomTypeDao.findById(parseInt(req.getParameter("id")));
            req.setAttribute("type", t);
            req.getRequestDispatcher("/WEB-INF/views/admin/type-form.jsp").forward(req, resp);
        } else if ("delete".equals(action)) {
            int id = parseInt(req.getParameter("id"));
            HttpSession session = req.getSession();
            if (roomTypeDao.countRoomsOfType(id) > 0) {
                session.setAttribute("flash", "该房型下还有房间，请先删除或转移这些房间");
            } else {
                roomTypeDao.delete(id);
                session.setAttribute("flash", "房型已删除");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/types");
        } else {
            req.setAttribute("types", roomTypeDao.findAll());
            passFlash(req);
            req.getRequestDispatcher("/WEB-INF/views/admin/types.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RoomType t = new RoomType();
        int id = parseInt(req.getParameter("id"));
        t.setId(id);
        t.setName(req.getParameter("name"));
        t.setPrice(new BigDecimal(req.getParameter("price")));
        t.setCapacity(parseInt(req.getParameter("capacity")));
        t.setBedType(req.getParameter("bedType"));
        t.setArea(parseInt(req.getParameter("area")));
        t.setAmenities(req.getParameter("amenities"));
        t.setDescription(req.getParameter("description"));
        t.setImage(req.getParameter("image"));

        HttpSession session = req.getSession();
        if (id > 0) {
            roomTypeDao.update(t);
            session.setAttribute("flash", "房型已更新");
        } else {
            roomTypeDao.insert(t);
            session.setAttribute("flash", "房型已新增");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/types");
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
