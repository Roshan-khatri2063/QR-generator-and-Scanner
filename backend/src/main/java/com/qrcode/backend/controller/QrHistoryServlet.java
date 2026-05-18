package com.qrcode.backend.controller;

import com.qrcode.backend.model.QrCode;
import com.qrcode.backend.services.QrService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * GET /api/qr/history
 *
 * Protected by AuthFilter — requires valid JWT.
 *
 * Returns all QR codes for the logged-in user, newest first.
 *
 * Response (200):
 * {
 *   "count": 3,
 *   "qrCodes": [
 *     { "id": 5, "content": "https://example.com", "createdAt": "2025-05-10 09:00:00" },
 *     ...
 *   ]
 * }
 *
 * Note: image_base64 is intentionally excluded from history list
 * to keep the response small. Load it on demand via GET /api/qr/{id}.
 */
@WebServlet("/api/qr/history")
public class QrHistoryServlet extends HttpServlet {

    private final QrService qrService = new QrService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        int userId = (int) request.getAttribute("userId");

        List<QrCode> history = qrService.getHistoryForUser(userId);

        PrintWriter out = response.getWriter();
        out.write("{\"count\":" + history.size() + ",\"qrCodes\":[");

        for (int i = 0; i < history.size(); i++) {
            QrCode qr = history.get(i);
            out.write("{"
                    + "\"id\":"          + qr.getId()                          + ","
                    + "\"content\":\""   + escapeJson(qr.getContent())         + "\","
                    + "\"createdAt\":\"" + qr.getCreatedAt().toString()        + "\""
                    + "}");
            if (i < history.size() - 1) out.write(",");
        }

        out.write("]}");
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
