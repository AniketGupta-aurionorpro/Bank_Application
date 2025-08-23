//package com.aurionpro.bank_application.Filters;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//
//@WebFilter({""})
//public class UserAuthFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
//            throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) servletRequest;
//        HttpServletResponse res = (HttpServletResponse) servletResponse;
//
//        HttpSession session = req.getSession(false);
//        boolean isLoggedIn = session != null && session.getAttribute("user") != null;
//
//        if (!isLoggedIn) {
//            res.sendRedirect(req.getContextPath() + "/index.jsp");
//        } else {
//            filterChain.doFilter(servletRequest, servletResponse); // continue request
//        }
//    }
//}
