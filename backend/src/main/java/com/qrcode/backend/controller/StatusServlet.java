package com.qrcode.backend.controller;

import com.qrcode.backend.util.DatabaseConnection;
import com.qrcode.backend.util.QrUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /api/status — public health check endpoint.
 * CORS is handled globally by CorsFilter.
 */
@WebServlet("/api/status")
public class StatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        boolean dbOk = DatabaseConnection.testConnection();

        String json = "{"
                + "\"message\":\"Backend API Running Successfully\","
                + "\"database\":\"" + (dbOk ? "connected" : "disconnected") + "\","
                + "\"status\":\""   + (dbOk ? "ok"        : "degraded")    + "\""
                + "}";

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(json);
    }
}
