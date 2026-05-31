package com.qrcode.backend.controller;

import com.qrcode.backend.services.QrScanService;
import com.qrcode.backend.util.QrUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

/**
 * POST /api/qr/scan
 *
 * Protected by AuthFilter — requires valid JWT.
 *
 * Accepts multipart/form-data with one field:
 *   image — the QR code image file (PNG, JPG, GIF, BMP)
 *
 * Returns on success (200):
 * {
 *   "result": "https://example.com",
 *   "isUrl":  true
 * }
 *
 * Returns on failure (400):
 * { "error": "No QR code found in the image." }
 */
@WebServlet("/api/qr/scan")
@MultipartConfig(
        maxFileSize    = 5 * 1024 * 1024,   // 5 MB max file size
        maxRequestSize = 6 * 1024 * 1024    // 6 MB max request size
)
public class QrScanServlet extends HttpServlet {

    private final QrScanService scanService = new QrScanService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the uploaded file part
        Part filePart = null;
        try {
            filePart = request.getPart("image");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(QrUtils.errorJson("No image file received."));
            return;
        }

        if (filePart == null || filePart.getSize() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(QrUtils.errorJson("Please upload an image file."));
            return;
        }

        // Validate file type
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(QrUtils.errorJson("Only image files are supported (PNG, JPG, GIF)."));
            return;
        }

        // Decode QR code from uploaded image stream
        try (InputStream imageStream = filePart.getInputStream()) {
            QrScanService.ScanResult result = scanService.decodeFromStream(imageStream);

            if (result.success) {
                boolean isUrl = result.text.startsWith("http://") ||
                        result.text.startsWith("https://") ||
                        result.text.startsWith("www.");

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(
                        "{"
                                + "\"result\":\"" + escapeJson(result.text) + "\","
                                + "\"isUrl\":"    + isUrl
                                + "}"
                );
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(QrUtils.errorJson(result.message));
            }
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
