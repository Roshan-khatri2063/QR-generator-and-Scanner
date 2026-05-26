import { useState } from "react";
import api from "../services/api.jsx";

function Generator() {
    const [text, setText]       = useState("");
    const [qrImage, setQrImage] = useState("");
    const [error, setError]     = useState("");
    const [loading, setLoading] = useState(false);

    const handleGenerate = async (e) => {
        e.preventDefault();
        setError("");
        setQrImage("");

        if (!text.trim()) {
            setError("Please enter text or a URL to generate a QR code.");
            return;
        }

        setLoading(true);
        try {
            // TODO: wire to POST /api/qr/generate when backend endpoint is ready
            const response = await api.post("/api/qr/generate", { content: text });
            setQrImage(response.data.image);   // expects { image: "<base64 png>" }
        } catch (err) {
            setError(err.friendlyMessage || "Failed to generate QR code. Is the backend running?");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: "480px", margin: "60px auto", padding: "24px" }}>
            <h2>Generate QR Code</h2>

            <form onSubmit={handleGenerate}>
                <div style={{ marginBottom: "16px" }}>
                    <label htmlFor="qr-text">Text or URL</label><br />
                    <input
                        id="qr-text"
                        type="text"
                        value={text}
                        onChange={(e) => setText(e.target.value)}
                        placeholder="https://example.com"
                        style={{ width: "100%", padding: "8px", marginTop: "4px" }}
                    />
                </div>

                <button type="submit" disabled={loading} style={{ padding: "10px 24px" }}>
                    {loading ? "Generating…" : "Generate"}
                </button>
            </form>

            {error && <p style={{ color: "red", marginTop: "16px" }}>{error}</p>}

            {qrImage && (
                <div style={{ marginTop: "24px", textAlign: "center" }}>
                    <img
                        src={`data:image/png;base64,${qrImage}`}
                        alt="Generated QR code"
                        style={{ border: "1px solid #ccc", padding: "8px" }}
                    />
                    <br />
                    <a
                        href={`data:image/png;base64,${qrImage}`}
                        download="qrcode.png"
                        style={{ marginTop: "8px", display: "inline-block" }}
                    >
                        Download QR Code
                    </a>
                </div>
            )}
        </div>
    );
}

export default Generator;
