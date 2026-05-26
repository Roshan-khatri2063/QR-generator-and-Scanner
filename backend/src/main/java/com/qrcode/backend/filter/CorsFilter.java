package com.qrcode.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Global CORS Filter — runs on every request before any other filter.
 *
 * Handles preflight OPTIONS requests and sets the correct
 * Access-Control-* headers on ALL responses so the browser
 * never blocks a request from the React dev server.
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Allow requests from the React dev server (and any localhost port)
        String origin = request.getHeader("Origin");
        if (origin != null && (origin.startsWith("http://localhost") ||
                origin.startsWith("http://127.0.0.1"))) {
            response.setHeader("Access-Control-Allow-Origin",      origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        }

        response.setHeader("Access-Control-Allow-Methods",  "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",  "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age",        "3600");

        // Handle preflight — respond immediately with 200, no further processing
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
