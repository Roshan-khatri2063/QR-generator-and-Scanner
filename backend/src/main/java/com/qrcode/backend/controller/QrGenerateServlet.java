package com.qrcode.backend.controller;

import com.qrcode.backend.services.QrService;
import com.qrcode.backend.util.QrUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * POST /api/qr/generate
 *
 * Protected by {@link com.qrcode.backend.filter.AuthFilter} — requires valid JWT.
 *
 * Accepts JSON body:
 * {
 *   "content": "https://example.com",
 *   "width":   300,   (optional, default 300, range 100–1000)
 *   "height":  300    (optional, default 300, range 100–1000)
 * }
 *
 * Returns on success (201):
 * {
 *   "qrId":   12,
 *   "image":  "<base64 PNG string>",
 *   "message":"QR code generated successfully."
 * }
 *
 * Returns on failure (400):
 * { "error": "Content cannot be empty." }
 */
@WebServlet("/api/qr/generate")
public class QrGenerateServlet extends HttpServlet {

    private final QrService qrService = new QrService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // userId injected by AuthFilter after JWT validation
        int userId = (int) request.getAttribute("userId");

        // Read JSON body
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }

        String bodyStr  = body.toString();
        String content  = extractJsonString(bodyStr, "content");
        int    width    = extractJsonInt(bodyStr, "width",  300);
        int    height   = extractJsonInt(bodyStr, "height", 300);

        // Delegate to service
        QrService.QrResult result = qrService.generateAndSave(userId, content, width, height);

        if (result.success) {
            response.setStatus(HttpServletResponse.SC_CREATED);  // 201
            response.getWriter().write(
                    "{"
                            + "\"qrId\":"    + result.qrId          + ","
                            + "\"image\":\""  + result.imageBase64   + "\","
                            + "\"message\":\"" + escapeJson(result.message) + "\""
                            + "}"
            );
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400
            response.getWriter().write(QrUtils.errorJson(result.message));
        }
    }

    // ------------------------------------------------------------------
    // JSON helpers
    // ------------------------------------------------------------------

    private String extractJsonString(String json, String key) {
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

    private int extractJsonInt(String json, String key, int defaultValue) {
        String search = "\"" + key + "\"";
        int keyIdx    = json.indexOf(search);
        if (keyIdx == -1) return defaultValue;
        int colonIdx  = json.indexOf(":", keyIdx + search.length());
        if (colonIdx == -1) return defaultValue;

        // Read digits after the colon (skip whitespace)
        int i = colonIdx + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

        StringBuilder digits = new StringBuilder();
        while (i < json.length() && (Character.isDigit(json.charAt(i)) || json.charAt(i) == '-')) {
            digits.append(json.charAt(i++));
        }
        try {
            return digits.isEmpty() ? defaultValue : Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
