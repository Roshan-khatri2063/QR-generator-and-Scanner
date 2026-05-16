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
 *
 * Accepts JSON body:
 * {
 *   "email":    "john@example.com",
 *   "password": "secret123"
 * }
 *
 * Returns on success (200):
 * {
 *   "message":  "Welcome back, john!",
 *   "token":    "<jwt>",
 *   "userId":   5,
 *   "username": "john",
 *   "email":    "john@example.com"
 * }
 *
 * Returns on failure (401):
 * { "error": "Invalid email or password." }
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    // ------------------------------------------------------------------
    // CORS
    // ------------------------------------------------------------------

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // ------------------------------------------------------------------
    // POST /api/auth/login
    // ------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // --- Read JSON body ---
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }

        String email    = extractJsonField(body.toString(), "email");
        String password = extractJsonField(body.toString(), "password");

        // --- Delegate to service ---
        AuthService.AuthResult result = authService.login(email, password);

        if (result.success) {
            // Fetch username from the token claims to include in response
            // (AuthService.login already verified the user and built the token)
            com.qrcode.backend.dao.Member member =
                    com.qrcode.backend.dao.Member.findByEmail(email.trim().toLowerCase())
                            .orElseThrow();

            response.setStatus(HttpServletResponse.SC_OK); // 200
            response.getWriter().write(
                    "{"
                            + "\"message\":\""  + escapeJson(result.message)        + "\","
                            + "\"token\":\""    + result.token                       + "\","
                            + "\"userId\":"     + result.userId                      + ","
                            + "\"username\":\"" + escapeJson(member.getUsername())   + "\","
                            + "\"email\":\""    + escapeJson(member.getEmail())      + "\""
                            + "}"
            );
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write(QrUtils.errorJson(result.message));
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private String extractJsonField(String json, String key) {
        String search   = "\"" + key + "\"";
        int keyIdx      = json.indexOf(search);
        if (keyIdx == -1) return "";
        int colonIdx    = json.indexOf(":", keyIdx + search.length());
        if (colonIdx == -1) return "";
        int openQuote   = json.indexOf("\"", colonIdx + 1);
        if (openQuote == -1) return "";
        int closeQuote  = json.indexOf("\"", openQuote + 1);
        if (closeQuote == -1) return "";
        return json.substring(openQuote + 1, closeQuote);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
