package com.qrcode.backend.filter;

import com.qrcode.backend.util.JwtUtil;
import com.qrcode.backend.util.QrUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * JWT Authentication Filter — protects all /api/qr/* endpoints.
 * CORS is handled globally by CorsFilter, not here.
 */
@WebFilter("/api/qr/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // OPTIONS preflight is already handled by CorsFilter — pass through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Extract and validate Bearer token
        String authHeader = request.getHeader("Authorization");
        String token      = JwtUtil.extractBearerToken(authHeader);

        if (token == null || !JwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    QrUtils.errorJson("Unauthorized. Please log in to continue.")
            );
            return;
        }

        // Token valid — inject userId and username as request attributes
        Claims claims = JwtUtil.validateToken(token);
        request.setAttribute("userId",   claims.get("userId",   Integer.class));
        request.setAttribute("username", claims.get("username", String.class));

        chain.doFilter(request, response);
    }
}
