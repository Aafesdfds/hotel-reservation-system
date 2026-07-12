package com.hotel.servlet.admin;

import com.google.gson.Gson;
import com.hotel.dao.ReservationDao;
import com.hotel.service.StatService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final StatService statService = new StatService();
    private final ReservationDao reservationDao = new ReservationDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("m", statService.dashboard());
        req.setAttribute("board", statService.roomStatusBoard());

        StatService.MonthlyChart chart = statService.monthlyChart(LocalDate.now().getYear());
        req.setAttribute("chartLabels", gson.toJson(chart.labels));
        req.setAttribute("chartRevenue", gson.toJson(chart.revenue));
        req.setAttribute("chartOccupancy", gson.toJson(chart.occupancy));

        // 最新几笔订单（列表页用 JSTL 控制只显示前几条）
        req.setAttribute("recentOrders", reservationDao.findAll(null, null));

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
