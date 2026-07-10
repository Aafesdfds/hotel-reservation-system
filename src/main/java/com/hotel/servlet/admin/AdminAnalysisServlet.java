package com.hotel.servlet.admin;

import com.hotel.service.AiService;
import com.hotel.util.AiClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** AI 经营分析：点击生成后调用大模型，返回分析文本 */
@WebServlet("/admin/analysis")
public class AdminAnalysisServlet extends HttpServlet {

    private final AiService aiService = new AiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("aiConfigured", AiClient.configured());
        req.getRequestDispatcher("/WEB-INF/views/admin/analysis.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AiService.AiResult result = aiService.analyze();
        req.setAttribute("result", result);
        req.setAttribute("aiConfigured", AiClient.configured());
        req.getRequestDispatcher("/WEB-INF/views/admin/analysis.jsp").forward(req, resp);
    }
}
