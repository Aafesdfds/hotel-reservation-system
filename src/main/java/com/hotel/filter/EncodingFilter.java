package com.hotel.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 统一设置请求和响应的编码为 UTF-8，防止中文乱码 */
@WebFilter("/*")
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (response instanceof HttpServletResponse) {
            response.setCharacterEncoding("UTF-8");
        }
        chain.doFilter(request, response);
    }
}
