package com.hotel.filter;

import com.hotel.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** 预订、我的订单、个人中心需要登录（任意用户）才能访问 */
@WebFilter(urlPatterns = {"/booking", "/orders", "/profile"})
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            // 记住原始请求，登录后跳回来
            String target = request.getRequestURI();
            String qs = request.getQueryString();
            if (qs != null) {
                target += "?" + qs;
            }
            session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", target);
            response.sendRedirect(request.getContextPath() + "/auth?action=login&msg=login");
            return;
        }
        chain.doFilter(req, resp);
    }
}
