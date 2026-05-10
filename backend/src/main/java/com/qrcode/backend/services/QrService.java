package com.qrcode.backend.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Service for QR code generation and scanning operations.
 */
public class QrService {

    private static final int DEFAULT_WIDTH  = 300;
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * Generates a QR code image as a Base64-encoded PNG string.
     *
     * @param content the text/URL to encode
     * @param width   image width in pixels
     * @param height  image height in pixels
     * @return Base64-encoded PNG image string
     * @throws WriterException if QR encoding fails
     * @throws IOException     if image writing fails
     */
    public String generateQrCodeBase64(String content, int width, int height)
            throws WriterException, IOException {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * Generates a QR code using default 300×300 dimensions.
     */
    public String generateQrCodeBase64(String content) throws WriterException, IOException {
        return generateQrCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
