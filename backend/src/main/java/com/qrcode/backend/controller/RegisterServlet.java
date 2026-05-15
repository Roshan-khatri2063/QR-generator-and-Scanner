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
 *
 * Accepts JSON body:
 * {
 *   "username": "john",
 *   "email":    "john@example.com",
 *   "password": "secret123"
 * }
 *
 * Returns on success (201):
 * { "message": "Registration successful! Welcome, john.", "userId": 5 }
 *
 * Returns on failure (400 / 409):
 * { "error": "An account with this email already exists." }
 */
@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        setCorsHeaders(res);
        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // ------------------------------------------------------------------
    // POST handler
    // ------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCorsHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // --- Read raw JSON body ---
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        // --- Parse JSON manually (no extra library needed) ---
        String username = extractJsonField(body.toString(), "username");
        String email    = extractJsonField(body.toString(), "email");
        String password = extractJsonField(body.toString(), "password");

        // --- Delegate to service ---
        AuthService.AuthResult result = authService.register(username, email, password);

        if (result.success) {
            response.setStatus(HttpServletResponse.SC_CREATED);   // 201
            response.getWriter().write(
                    "{\"message\":\"" + escapeJson(result.message) + "\","
                            + "\"userId\":"   + result.userId + "}"
            );
        } else {
            // 409 Conflict for duplicates, 400 for validation errors
            boolean isDuplicate = result.message.contains("already");
            response.setStatus(isDuplicate
                    ? HttpServletResponse.SC_CONFLICT            // 409
                    : HttpServletResponse.SC_BAD_REQUEST);       // 400
            response.getWriter().write(QrUtils.errorJson(result.message));
        }
    }

    // ------------------------------------------------------------------
    // Minimal JSON field extractor
    // Handles simple string values: "key": "value"
    // ------------------------------------------------------------------

    private String extractJsonField(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx == -1) return "";

        int colonIdx = json.indexOf(":", keyIdx + search.length());
        if (colonIdx == -1) return "";

        int openQuote = json.indexOf("\"", colonIdx + 1);
        if (openQuote == -1) return "";

        int closeQuote = json.indexOf("\"", openQuote + 1);
        if (closeQuote == -1) return "";

        return json.substring(openQuote + 1, closeQuote);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
