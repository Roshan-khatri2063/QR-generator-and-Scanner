import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api.jsx";

function Register() {
    const [username, setUsername]       = useState("");
    const [email, setEmail]             = useState("");
    const [password, setPassword]       = useState("");
    const [error, setError]             = useState("");
    const [success, setSuccess]         = useState("");
    const [loading, setLoading]         = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        // --- Client-side validation ---
        if (!username.trim() || !email.trim() || !password) {
            setError("Please fill in all fields.");
            return;
        }
        if (username.trim().length < 3) {
            setError("Username must be at least 3 characters.");
            return;
        }
        if (!/^[A-Za-z0-9_]+$/.test(username)) {
            setError("Username may only contain letters, numbers, and underscores.");
            return;
        }
        if (password.length < 6) {
            setError("Password must be at least 6 characters.");
            return;
        }

        setLoading(true);
        try {
            const response = await api.post("/api/auth/register", {
                username: username.trim(),
                email:    email.trim().toLowerCase(),
                password,
            });

            setSuccess(response.data.message || "Registration successful! Redirecting to login…");

            // Redirect to login after 1.5 seconds
            setTimeout(() => navigate("/login"), 1500);

        } catch (err) {
            const msg = err.response?.data?.error || "Registration failed. Please try again.";
            setError(msg);
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
            <h2 style={{ marginTop: 0 }}>Create an Account</h2>

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

            {success && (
                <div style={{
                    padding: "10px 14px",
                    background: "#eafaf1",
                    color: "#1e8449",
                    borderRadius: "6px",
                    marginBottom: "16px",
                    fontSize: "14px"
                }}>
                    {success}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "16px" }}>
                    <label htmlFor="username" style={{ fontWeight: "600", fontSize: "14px" }}>
                        Username
                    </label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="johndoe"
                        disabled={loading}
                        style={{
                            display: "block", width: "100%", padding: "10px",
                            marginTop: "6px", borderRadius: "6px",
                            border: "1px solid #ccc", boxSizing: "border-box", fontSize: "15px"
                        }}
                    />
                </div>

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
                        placeholder="Min. 6 characters"
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
                    {loading ? "Creating account…" : "Register"}
                </button>
            </form>

            <p style={{ marginTop: "20px", textAlign: "center", fontSize: "14px" }}>
                Already have an account?{" "}
                <Link to="/login" style={{ color: "#2980b9", fontWeight: "600" }}>
                    Login
                </Link>
            </p>
        </div>
    );
}

export default Register;
