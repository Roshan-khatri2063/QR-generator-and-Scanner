package com.qrcode.backend.model;

import java.sql.Timestamp;

/**
 * Model representing a single QR code record in the {@code qr_codes} table.
 */
public class QrCode {

    private int       id;
    private int       userId;
    private String    content;       // original text / URL encoded
    private String    imageBase64;   // Base64 PNG image from ZXing
    private Timestamp createdAt;

    public QrCode() {}

    public QrCode(int id, int userId, String content, String imageBase64, Timestamp createdAt) {
        this.id          = id;
        this.userId      = userId;
        this.content     = content;
        this.imageBase64 = imageBase64;
        this.createdAt   = createdAt;
    }

    // Getters & Setters
    public int       getId()                          { return id; }
    public void      setId(int id)                    { this.id = id; }

    public int       getUserId()                      { return userId; }
    public void      setUserId(int userId)            { this.userId = userId; }

    public String    getContent()                     { return content; }
    public void      setContent(String content)       { this.content = content; }

    public String    getImageBase64()                 { return imageBase64; }
    public void      setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public Timestamp getCreatedAt()                   { return createdAt; }
    public void      setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "QrCode{id=" + id + ", userId=" + userId + ", content='" + content + "'}";
    }
}
