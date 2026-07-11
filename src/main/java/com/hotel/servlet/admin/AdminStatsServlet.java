package com.hotel.servlet.admin;

import com.google.gson.Gson;
import com.hotel.service.StatService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 数据统计页：月度营收、入住率、房型分布、订单状态分布等图表 */
@WebServlet("/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    private final StatService statService = new StatService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        StatService.MonthlyChart chart = statService.monthlyChart(LocalDate.now().getYear());
        req.setAttribute("chartLabels", gson.toJson(chart.labels));
        req.setAttribute("chartRevenue", gson.toJson(chart.revenue));
        req.setAttribute("chartOrders", gson.toJson(chart.orders));
        req.setAttribute("chartOccupancy", gson.toJson(chart.occupancy));

        // 房型分布
        List<String> typeNames = new ArrayList<>();
        List<Integer> typeCounts = new ArrayList<>();
        List<Object> typeRevenue = new ArrayList<>();
        for (Object[] row : statService.typeDistribution()) {
            typeNames.add((String) row[0]);
            typeCounts.add((Integer) row[1]);
            typeRevenue.add(row[2]);
        }
        req.setAttribute("typeNames", gson.toJson(typeNames));
        req.setAttribute("typeCounts", gson.toJson(typeCounts));
        req.setAttribute("typeRevenue", gson.toJson(typeRevenue));

        // 订单状态分布（饼图）
        List<Map<String, Object>> statusData = new ArrayList<>();
        for (Map.Entry<String, Integer> e : statService.statusDistribution().entrySet()) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("name", e.getKey());
            item.put("value", e.getValue());
            statusData.add(item);
        }
        req.setAttribute("statusData", gson.toJson(statusData));

        req.getRequestDispatcher("/WEB-INF/views/admin/stats.jsp").forward(req, resp);
    }
}
