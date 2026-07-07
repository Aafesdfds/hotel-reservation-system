package com.hotel.servlet;

import com.hotel.dao.RoomTypeDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final RoomTypeDao roomTypeDao = new RoomTypeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("types", roomTypeDao.findAll());
        req.getRequestDispatcher("/WEB-INF/views/user/home.jsp").forward(req, resp);
    }
}
