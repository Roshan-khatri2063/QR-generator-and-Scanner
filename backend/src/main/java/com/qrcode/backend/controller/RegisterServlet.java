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
 * POST /api/auth/register
 * CORS is handled globally by CorsFilter.
 */
@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

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

        String username = extractJsonField(body.toString(), "username");
        String email    = extractJsonField(body.toString(), "email");
        String password = extractJsonField(body.toString(), "password");

        AuthService.AuthResult result = authService.register(username, email, password);

        if (result.success) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(
                    "{\"message\":\"" + escapeJson(result.message) + "\","
                            + "\"userId\":"     + result.userId              + "}"
            );
        } else {
            boolean isDuplicate = result.message.contains("already");
            response.setStatus(isDuplicate
                    ? HttpServletResponse.SC_CONFLICT
                    : HttpServletResponse.SC_BAD_REQUEST);
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
