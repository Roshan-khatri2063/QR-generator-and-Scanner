package com.qrcode.backend.controller;

import com.qrcode.backend.services.AuthService;
import com.qrcode.backend.util.QrUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * POST /api/auth/login
 * CORS is handled globally by CorsFilter.
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }

        String email    = extractJsonField(body.toString(), "email");
        String password = extractJsonField(body.toString(), "password");

        AuthService.AuthResult result = authService.login(email, password);

        if (result.success) {
            com.qrcode.backend.dao.Member member =
                    com.qrcode.backend.dao.Member.findByEmail(email.trim().toLowerCase())
                            .orElseThrow();

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    "{"
                            + "\"message\":\""  + escapeJson(result.message)      + "\","
                            + "\"token\":\""    + result.token                     + "\","
                            + "\"userId\":"     + result.userId                    + ","
                            + "\"username\":\"" + escapeJson(member.getUsername()) + "\","
                            + "\"email\":\""    + escapeJson(member.getEmail())    + "\""
                            + "}"
            );
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(QrUtils.errorJson(result.message));
        }
    }

    private String extractJsonField(String json, String key) {
        String search  = "\"" + key + "\"";
        int keyIdx     = json.indexOf(search);
        if (keyIdx == -1) return "";
        int colonIdx   = json.indexOf(":", keyIdx + search.length());
        if (colonIdx == -1) return "";
        int openQuote  = json.indexOf("\"", colonIdx + 1);
        if (openQuote == -1) return "";
        int closeQuote = json.indexOf("\"", openQuote + 1);
        if (closeQuote == -1) return "";
        return json.substring(openQuote + 1, closeQuote);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
