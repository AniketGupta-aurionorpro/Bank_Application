package com.aurionpro.bank_application.Filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter({"/login"})
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);

        if (session != null && "admin".equals(session.getAttribute("role"))
        ) {
            res.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        } else if (session != null && "customer".equals(session.getAttribute("role"))
        ) {
            res.sendRedirect(req.getContextPath() + "/customer/home");
            return;
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}

