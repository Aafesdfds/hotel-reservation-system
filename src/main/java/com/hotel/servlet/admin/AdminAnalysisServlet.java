package com.hotel.servlet.admin;

import com.hotel.service.AiService;
import com.hotel.util.AiClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/** AI 经营分析：生成经营分析报告，可在线查看、下载 Word 文档、打印导出 PDF */
@WebServlet("/admin/analysis")
public class AdminAnalysisServlet extends HttpServlet {

    private final AiService aiService = new AiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if ("word".equals(req.getParameter("action"))) {
            // 下载 Word：优先用刚才已生成的报告，避免重复调大模型；会话里没有就现生成一份
            AiService.AiResult result = (AiService.AiResult) session.getAttribute("lastAnalysis");
            if (result == null) {
                result = aiService.analyze();
                session.setAttribute("lastAnalysis", result);
            }
            req.setAttribute("result", result);
            req.setAttribute("reportDate", LocalDate.now().toString());
            String encoded = URLEncoder.encode("云栖酒店经营分析报告.doc", StandardCharsets.UTF_8.name())
                    .replace("+", "%20");
            resp.setHeader("Content-Disposition",
                    "attachment; filename=\"report.doc\"; filename*=UTF-8''" + encoded);
            req.getRequestDispatcher("/WEB-INF/views/admin/analysis-word.jsp").forward(req, resp);
            return;
        }
        // 普通进入页面：本次会话已经生成过报告的话，把上次的带出来继续显示
        AiService.AiResult last = (AiService.AiResult) session.getAttribute("lastAnalysis");
        if (last != null) {
            req.setAttribute("result", last);
        }
        req.setAttribute("reportDate", LocalDate.now().toString());
        req.setAttribute("aiConfigured", AiClient.configured());
        req.getRequestDispatcher("/WEB-INF/views/admin/analysis.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AiService.AiResult result = aiService.analyze();
        req.getSession().setAttribute("lastAnalysis", result);
        req.setAttribute("result", result);
        req.setAttribute("reportDate", LocalDate.now().toString());
        req.setAttribute("aiConfigured", AiClient.configured());
        req.getRequestDispatcher("/WEB-INF/views/admin/analysis.jsp").forward(req, resp);
    }
}
