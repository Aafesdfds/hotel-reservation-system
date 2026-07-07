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

/** 个人中心：改资料、改密码 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        if ("password".equals(action)) {
            try {
                userService.changePassword(user.getId(),
                        req.getParameter("oldPwd"), req.getParameter("newPwd"));
                req.setAttribute("msg", "密码修改成功");
            } catch (BookingException e) {
                req.setAttribute("error", e.getMessage());
            }
        } else {
            user.setRealName(req.getParameter("realName"));
            user.setPhone(req.getParameter("phone"));
            user.setGender(req.getParameter("gender"));
            userService.updateProfile(user);
            session.setAttribute("user", user);
            req.setAttribute("msg", "资料保存成功");
        }
        req.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(req, resp);
    }
}
