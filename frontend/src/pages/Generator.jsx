import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api.jsx";


function Generator() {
    const [content, setContent]   = useState("");
    const [size, setSize]         = useState(300);
    const [qrImage, setQrImage]   = useState("");
    const [qrId, setQrId]         = useState(null);
    const [error, setError]       = useState("");
    const [loading, setLoading]   = useState(false);


    const navigate       = useNavigate();

    const handleGenerate = async (e) => {
        e.preventDefault();
        setError("");
        setQrImage("");
        setQrId(null);

        if (!content.trim()) {
            setError("Please enter some text or a URL.");
            return;
        }
        if (content.length > 2000) {
            setError("Content is too long (max 2000 characters).");
            return;
        }

        setLoading(true);
        try {
            const response = await api.post("/api/qr/generate", {
                content: content.trim(),
                width:   size,
                height:  size,
            });

            // Backend returns: { qrId, image, message }
            setQrImage(response.data.image);
            setQrId(response.data.qrId);

        } catch (err) {
            if (err.response?.status === 401) {
                navigate("/login");
            } else {
                setError(err.friendlyMessage || "Failed to generate QR code. Is the backend running?");
                console.error(err);
            }
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        setContent("");
        setQrImage("");
        setQrId(null);
        setError("");
    };

    return (
        <div style={{ maxWidth: "540px", margin: "50px auto", padding: "32px" }}>
            <h2 style={{ marginTop: 0 }}>Generate QR Code</h2>

            <form onSubmit={handleGenerate}>
                <div style={{ marginBottom: "16px" }}>
                    <label htmlFor="content" style={{ fontWeight: "600", fontSize: "14px" }}>
                        Text or URL
                    </label>
                    <textarea
                        id="content"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        placeholder="https://example.com or any text…"
                        rows={3}
                        disabled={loading}
                        style={{
                            display: "block", width: "100%", padding: "10px",
                            marginTop: "6px", borderRadius: "6px",
                            border: "1px solid #ccc", boxSizing: "border-box",
                            fontSize: "15px", resize: "vertical"
                        }}
                    />
                    <small style={{ color: "#888" }}>{content.length} / 2000 characters</small>
                </div>

                <div style={{ marginBottom: "24px" }}>
                    <label htmlFor="size" style={{ fontWeight: "600", fontSize: "14px" }}>
                        Size: {size}×{size} px
                    </label>
                    <input
                        id="size"
                        type="range"
                        min={100} max={600} step={50}
                        value={size}
                        onChange={(e) => setSize(Number(e.target.value))}
                        disabled={loading}
                        style={{ display: "block", width: "100%", marginTop: "8px" }}
                    />
                    <div style={{ display: "flex", justifyContent: "space-between",
                        fontSize: "12px", color: "#888" }}>
                        <span>100px</span><span>600px</span>
                    </div>
                </div>

                <div style={{ display: "flex", gap: "12px" }}>
                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            flex: 1, padding: "12px",
                            background: loading ? "#95a5a6" : "#2c3e50",
                            color: "#fff", border: "none", borderRadius: "6px",
                            fontSize: "15px", fontWeight: "600",
                            cursor: loading ? "not-allowed" : "pointer"
                        }}
                    >
                        {loading ? "Generating…" : "Generate QR Code"}
                    </button>

                    {qrImage && (
                        <button
                            type="button"
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
            </form>

            {error && (
                <div style={{
                    marginTop: "16px", padding: "10px 14px",
                    background: "#fdecea", color: "#c0392b",
                    borderRadius: "6px", fontSize: "14px"
                }}>
                    {error}
                </div>
            )}

            {qrImage && (
                <div style={{
                    marginTop: "32px", textAlign: "center",
                    padding: "24px", border: "1px solid #e0e0e0",
                    borderRadius: "10px", background: "#fafafa"
                }}>
                    <p style={{ margin: "0 0 12px", fontSize: "13px", color: "#888" }}>
                        QR #{qrId} — saved to your history ✓
                    </p>
                    <img
                        src={`data:image/png;base64,${qrImage}`}
                        alt="Generated QR code"
                        style={{
                            maxWidth: "100%", border: "1px solid #ddd",
                            padding: "8px", background: "#fff", borderRadius: "6px"
                        }}
                    />
                    <div style={{ marginTop: "16px" }}>
                        <a
                            href={`data:image/png;base64,${qrImage}`}
                            download={`qrcode-${qrId}.png`}
                            style={{
                                display: "inline-block", padding: "10px 24px",
                                background: "#27ae60", color: "#fff",
                                borderRadius: "6px", textDecoration: "none",
                                fontWeight: "600", fontSize: "14px"
                            }}
                        >
                            ⬇ Download PNG
                        </a>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Generator;
