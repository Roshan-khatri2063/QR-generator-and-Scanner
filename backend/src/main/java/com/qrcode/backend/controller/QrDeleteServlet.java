package com.qrcode.backend.controller;

import com.qrcode.backend.services.QrService;
import com.qrcode.backend.util.QrUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * DELETE /api/qr/delete/{id}
 *
 * Protected by AuthFilter — requires valid JWT.
 * Only deletes if the QR code belongs to the requesting user.
 *
 * Returns (200): { "message": "QR code deleted." }
 * Returns (403): { "error": "Not found or access denied." }
 * Returns (400): { "error": "Invalid QR code ID." }
 */
@WebServlet("/api/qr/delete/*")
public class QrDeleteServlet extends HttpServlet {

    private final QrService qrService = new QrService();

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int userId = (int) request.getAttribute("userId");

        // Extract the ID from the path: /api/qr/delete/42  →  42
        String pathInfo = request.getPathInfo();  // "/42"
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(QrUtils.errorJson("QR code ID is required in the path."));
            return;
        }

        int qrId;
        try {
            qrId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(QrUtils.errorJson("Invalid QR code ID."));
            return;
        }

        boolean deleted = qrService.delete(qrId, userId);

        if (deleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(QrUtils.successJson("QR code deleted."));
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(QrUtils.errorJson("Not found or access denied."));
        }
    }
}
