
package com.qrcode.backend.services;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for decoding QR codes from uploaded images using ZXing.
 *
 * <p>Day 6: Accepts raw image bytes (PNG, JPG, GIF, BMP),
 * decodes the QR code, and returns the embedded text or URL.</p>
 */
public class QrScanService {

    private static final Logger LOGGER = Logger.getLogger(QrScanService.class.getName());

    // ------------------------------------------------------------------
    // Result wrapper
    // ------------------------------------------------------------------

    public static class ScanResult {
        public final boolean success;
        public final String  text;     // decoded content on success
        public final String  message;  // error message on failure

        private ScanResult(boolean success, String text, String message) {
            this.success = success;
            this.text    = text;
            this.message = message;
        }

        public static ScanResult ok(String text) {
            return new ScanResult(true, text, null);
        }

        public static ScanResult fail(String message) {
            return new ScanResult(false, null, message);
        }
    }

    // ------------------------------------------------------------------
    // Decode from raw bytes
    // ------------------------------------------------------------------

    /**
     * Decodes a QR code from raw image bytes.
     *
     * @param imageBytes the uploaded image file bytes (PNG, JPG, etc.)
     * @return ScanResult with decoded text, or failure reason
     */
    public ScanResult decode(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return ScanResult.fail("No image data received.");
        }

        try {
            return decodeFromStream(new ByteArrayInputStream(imageBytes));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "QR decode failed: " + e.getMessage(), e);
            return ScanResult.fail("Failed to process image. Please try a clearer image.");
        }
    }

    /**
     * Decodes a QR code from an InputStream.
     *
     * @param inputStream the image input stream
     * @return ScanResult with decoded text, or failure reason
     */
    public ScanResult decodeFromStream(InputStream inputStream) {
        try {
            // Read image
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                return ScanResult.fail("Could not read image. Make sure it is a valid PNG, JPG, or GIF.");
            }

            // Convert to ZXing luminance source
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap    = new BinaryBitmap(new HybridBinarizer(source));

            // Set decode hints for better accuracy
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS,
                    java.util.Arrays.asList(BarcodeFormat.QR_CODE));

            // Decode
            Result result = new MultiFormatReader().decode(bitmap, hints);
            String text   = result.getText();

            LOGGER.info("QR decoded successfully: " + text);
            return ScanResult.ok(text);

        } catch (NotFoundException e) {
            return ScanResult.fail("No QR code found in the image. Please try a clearer or better-lit image.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Image read error: " + e.getMessage(), e);
            return ScanResult.fail("Failed to read image file.");
        }
    }
}
