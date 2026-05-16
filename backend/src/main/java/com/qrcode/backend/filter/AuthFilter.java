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
 * JWT Authentication Filter.
 *
 * <p>Intercepts all requests to {@code /api/qr/*} (protected endpoints)
 * and rejects any request that does not carry a valid Bearer token.</p>
 *
 * <p>On success, the filter adds two request attributes that downstream
 * servlets can use:</p>
 * <ul>
 *   <li>{@code userId}   — Integer</li>
 *   <li>{@code username} — String</li>
 * </ul>
 *
 * <p>Public endpoints ({@code /api/auth/*}, {@code /api/status}, etc.)
 * are not mapped to this filter and pass through freely.</p>
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

        // CORS headers on every response (including 401s)
        response.setHeader("Access-Control-Allow-Origin",  "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Pass OPTIONS preflight through without auth check
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // Extract and validate Bearer token
        String authHeader = request.getHeader("Authorization");
        String token      = JwtUtil.extractBearerToken(authHeader);

        if (token == null || !JwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    QrUtils.errorJson("Unauthorized. Please log in to continue.")
            );
            return;
        }

        // Token is valid — parse claims and attach to request
        Claims claims = JwtUtil.validateToken(token);
        request.setAttribute("userId",   claims.get("userId",   Integer.class));
        request.setAttribute("username", claims.get("username", String.class));

        // Proceed to the servlet
        chain.doFilter(request, response);
    }
}
