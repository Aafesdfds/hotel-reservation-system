package com.hotel.servlet;

import com.hotel.model.User;
import com.hotel.service.BookingException;
import com.hotel.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** 登录、注册、退出 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        if ("register".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, resp);
            return;
        }
        // 默认登录页
        String msg = req.getParameter("msg");
        if ("login".equals(msg)) {
            req.setAttribute("tip", "请先登录后再操作");
        } else if ("admin".equals(msg)) {
            req.setAttribute("tip", "该页面需要管理员登录");
        }
        req.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("register".equals(action)) {
            doRegister(req, resp);
        } else {
            doLogin(req, resp);
        }
    }

    private void doLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        User user = userService.login(username, password);
        if (user == null) {
            req.setAttribute("error", "用户名或密码不正确");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(req, resp);
            return;
        }
        user.setPassword(null);
        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);

        String redirect = (String) session.getAttribute("redirectAfterLogin");
        session.removeAttribute("redirectAfterLogin");
        if (redirect != null && !redirect.isEmpty()) {
            resp.sendRedirect(redirect);
        } else if (user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    private void doRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        String realName = req.getParameter("realName");
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");

        if (password == null || !password.equals(confirm)) {
            fail(req, resp, "两次输入的密码不一致", username, realName, phone);
            return;
        }
        try {
            User user = userService.register(username, password, realName, phone, gender);
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (BookingException e) {
            fail(req, resp, e.getMessage(), username, realName, phone);
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String error,
                      String username, String realName, String phone)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("username", username);
        req.setAttribute("realName", realName);
        req.setAttribute("phone", phone);
        req.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(req, resp);
    }
}
