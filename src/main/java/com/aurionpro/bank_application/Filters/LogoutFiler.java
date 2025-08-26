package com.aurionpro.bank_application.Filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class LogoutFiler implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);

        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);

        boolean isIndexPage = uri.endsWith("/") || uri.endsWith("/index.jsp") || uri.endsWith("/login");
        boolean isStatic = uri.contains("/CSS/") || uri.contains("/JS/") || uri.contains("/IMG/");

        if (!isLoggedIn && !isIndexPage && !isStatic) {
            res.sendRedirect(req.getContextPath() + "/");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}