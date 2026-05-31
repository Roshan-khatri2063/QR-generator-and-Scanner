import { useState, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api.jsx";

function Scan() {
    const [result, setResult]         = useState(null);   // { text, isUrl }
    const [preview, setPreview]       = useState(null);   // image preview URL
    const [error, setError]           = useState("");
    const [loading, setLoading]       = useState(false);
    const [dragOver, setDragOver]     = useState(false);

    const fileInputRef = useRef(null);
    const navigate     = useNavigate();

    // ------------------------------------------------------------------
    // Handle file selection (from input or drag-and-drop)
    // ------------------------------------------------------------------
    const handleFile = useCallback(async (file) => {
        if (!file) return;

        // Validate type
        if (!file.type.startsWith("image/")) {
            setError("Only image files are supported (PNG, JPG, GIF).");
            return;
        }

        // Validate size (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            setError("Image is too large. Maximum size is 5MB.");
            return;
        }

        // Show preview
        const reader = new FileReader();
        reader.onload = (e) => setPreview(e.target.result);
        reader.readAsDataURL(file);

        // Reset previous result
        setResult(null);
        setError("");
        setLoading(true);

        try {
            // Send as multipart/form-data
            const formData = new FormData();
            formData.append("image", file);

            const response = await api.post("/api/qr/scan", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            setResult({
                text:  response.data.result,
                isUrl: response.data.isUrl,
            });

        } catch (err) {
            if (err.response?.status === 401) {
                navigate("/login");
            } else {
                setError(err.friendlyMessage || "Failed to scan QR code. Please try a clearer image.");
            }
        } finally {
            setLoading(false);
        }
    }, [navigate]);

    // ------------------------------------------------------------------
    // Drag and drop handlers
    // ------------------------------------------------------------------
    const handleDrop = (e) => {
        e.preventDefault();
        setDragOver(false);
        const file = e.dataTransfer.files[0];
        handleFile(file);
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setDragOver(true);
    };

    const handleDragLeave = () => setDragOver(false);

    // ------------------------------------------------------------------
    // Reset
    // ------------------------------------------------------------------
    const handleReset = () => {
        setResult(null);
        setPreview(null);
        setError("");
        if (fileInputRef.current) fileInputRef.current.value = "";
    };

    // ------------------------------------------------------------------
    // Copy to clipboard
    // ------------------------------------------------------------------
    const handleCopy = () => {
        if (result?.text) {
            navigator.clipboard.writeText(result.text);
            alert("Copied to clipboard!");
        }
    };

    return (
        <div style={{ maxWidth: "560px", margin: "50px auto", padding: "32px" }}>
            <h2 style={{ marginTop: 0 }}>Scan QR Code</h2>
            <p style={{ color: "#666", marginBottom: "24px" }}>
                Upload a QR code image to decode its content.
            </p>

            {/* Drop Zone */}
            <div
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onClick={() => fileInputRef.current?.click()}
                style={{
                    border: `2px dashed ${dragOver ? "#2c3e50" : "#ccc"}`,
                    borderRadius: "10px",
                    padding: "40px 20px",
                    textAlign: "center",
                    cursor: "pointer",
                    background: dragOver ? "#f0f4f8" : "#fafafa",
                    transition: "all 0.2s",
                    marginBottom: "20px"
                }}
            >
                {preview ? (
                    <img
                        src={preview}
                        alt="QR preview"
                        style={{
                            maxWidth: "200px", maxHeight: "200px",
                            borderRadius: "6px", border: "1px solid #ddd"
                        }}
                    />
                ) : (
                    <>
                        <div style={{ fontSize: "48px", marginBottom: "12px" }}>📷</div>
                        <p style={{ margin: 0, fontWeight: "600", color: "#2c3e50" }}>
                            Click or drag & drop an image here
                        </p>
                        <p style={{ margin: "6px 0 0", fontSize: "13px", color: "#888" }}>
                            PNG, JPG, GIF — max 5MB
                        </p>
                    </>
                )}
            </div>

            {/* Hidden file input */}
            <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                style={{ display: "none" }}
                onChange={(e) => handleFile(e.target.files[0])}
            />

            {/* Action buttons */}
            <div style={{ display: "flex", gap: "12px", marginBottom: "20px" }}>
                <button
                    onClick={() => fileInputRef.current?.click()}
                    disabled={loading}
                    style={{
                        flex: 1, padding: "12px",
                        background: loading ? "#95a5a6" : "#2c3e50",
                        color: "#fff", border: "none", borderRadius: "6px",
                        fontSize: "15px", fontWeight: "600",
                        cursor: loading ? "not-allowed" : "pointer"
                    }}
                >
                    {loading ? "Scanning…" : "📂 Choose Image"}
                </button>

                {(preview || result) && (
                    <button
                        onClick={handleReset}
                        style={{
                            padding: "12px 20px",
                            background: "#ecf0f1", color: "#2c3e50",
                            border: "1px solid #bdc3c7", borderRadius: "6px",
                            fontSize: "15px", cursor: "pointer"
                        }}
                    >
                        Reset
                    </button>
                )}
            </div>

            {/* Loading indicator */}
            {loading && (
                <div style={{
                    padding: "16px", textAlign: "center",
                    color: "#2c3e50", fontSize: "14px"
                }}>
                    🔍 Scanning image for QR code…
                </div>
            )}

            {/* Error */}
            {error && (
                <div style={{
                    padding: "12px 16px",
                    background: "#fdecea", color: "#c0392b",
                    borderRadius: "6px", fontSize: "14px",
                    marginBottom: "16px"
                }}>
                    ⚠ {error}
                </div>
            )}

            {/* Success result */}
            {result && (
                <div style={{
                    padding: "20px",
                    border: "1px solid #d5f5e3",
                    borderRadius: "10px",
                    background: "#eafaf1"
                }}>
                    <p style={{
                        margin: "0 0 10px",
                        fontWeight: "700",
                        color: "#1e8449",
                        fontSize: "15px"
                    }}>
                        ✓ QR Code Decoded Successfully
                    </p>

                    <div style={{
                        padding: "12px",
                        background: "#fff",
                        borderRadius: "6px",
                        border: "1px solid #d5f5e3",
                        wordBreak: "break-all",
                        fontSize: "14px",
                        marginBottom: "14px"
                    }}>
                        {result.isUrl ? (
                            <a
                                href={result.text}
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{ color: "#2980b9", fontWeight: "600" }}
                            >
                                🔗 {result.text}
                            </a>
                        ) : (
                            <span style={{ color: "#2c3e50" }}>{result.text}</span>
                        )}
                    </div>

                    <div style={{ display: "flex", gap: "10px" }}>
                        <button
                            onClick={handleCopy}
                            style={{
                                padding: "8px 18px",
                                background: "#2c3e50", color: "#fff",
                                border: "none", borderRadius: "6px",
                                fontSize: "13px", cursor: "pointer",
                                fontWeight: "600"
                            }}
                        >
                            📋 Copy Text
                        </button>

                        {result.isUrl && (
                            <a
                                href={result.text}
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{
                                    padding: "8px 18px",
                                    background: "#27ae60", color: "#fff",
                                    borderRadius: "6px", textDecoration: "none",
                                    fontSize: "13px", fontWeight: "600"
                                }}
                            >
                                🌐 Open URL
                            </a>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Scan;
