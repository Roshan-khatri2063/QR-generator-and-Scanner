import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api.jsx";
import { useAuth } from "../context/AuthContext.jsx";

function Login() {
    const [email, setEmail]       = useState("");
    const [password, setPassword] = useState("");
    const [error, setError]       = useState("");
    const [loading, setLoading]   = useState(false);

    const { loginUser } = useAuth();
    const navigate      = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Client-side quick check
        if (!email.trim() || !password) {
            setError("Please fill in all fields.");
            return;
        }

        setLoading(true);
        try {
            const response = await api.post("/api/auth/login", {
                email:    email.trim().toLowerCase(),
                password,
            });

            const { token, userId, username, email: userEmail } = response.data;

            // Store user + token in global AuthContext
            loginUser({ id: userId, username, email: userEmail }, token);

            // Redirect to home
            navigate("/");

        } catch (err) {
            setError(err.friendlyMessage || "Login failed. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            maxWidth: "440px",
            margin: "60px auto",
            padding: "32px",
            borderRadius: "10px",
            border: "1px solid #e0e0e0",
            boxShadow: "0 2px 12px rgba(0,0,0,0.07)"
        }}>
            <h2 style={{ marginTop: 0 }}>Login to QRHub</h2>

            {error && (
                <div style={{
                    padding: "10px 14px",
                    background: "#fdecea",
                    color: "#c0392b",
                    borderRadius: "6px",
                    marginBottom: "16px",
                    fontSize: "14px"
                }}>
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "16px" }}>
                    <label htmlFor="email" style={{ fontWeight: "600", fontSize: "14px" }}>
                        Email
                    </label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="you@example.com"
                        disabled={loading}
                        style={{
                            display: "block", width: "100%", padding: "10px",
                            marginTop: "6px", borderRadius: "6px",
                            border: "1px solid #ccc", boxSizing: "border-box", fontSize: "15px"
                        }}
                    />
                </div>

                <div style={{ marginBottom: "24px" }}>
                    <label htmlFor="password" style={{ fontWeight: "600", fontSize: "14px" }}>
                        Password
                    </label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                        disabled={loading}
                        style={{
                            display: "block", width: "100%", padding: "10px",
                            marginTop: "6px", borderRadius: "6px",
                            border: "1px solid #ccc", boxSizing: "border-box", fontSize: "15px"
                        }}
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    style={{
                        width: "100%", padding: "12px",
                        background: loading ? "#95a5a6" : "#2c3e50",
                        color: "#fff", border: "none",
                        borderRadius: "6px", fontSize: "16px",
                        cursor: loading ? "not-allowed" : "pointer",
                        fontWeight: "600"
                    }}
                >
                    {loading ? "Logging in…" : "Login"}
                </button>
            </form>

            <p style={{ marginTop: "20px", textAlign: "center", fontSize: "14px" }}>
                Don&apos;t have an account?{" "}
                <Link to="/register" style={{ color: "#2980b9", fontWeight: "600" }}>
                    Register
                </Link>
            </p>
        </div>
    );
}

export default Login;
