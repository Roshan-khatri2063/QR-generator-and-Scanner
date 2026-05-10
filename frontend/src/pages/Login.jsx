import { useState } from "react";
import { Link } from "react-router-dom";

function Login() {
    const [email, setEmail]       = useState("");
    const [password, setPassword] = useState("");
    const [error, setError]       = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!email || !password) {
            setError("Please fill in all fields.");
            return;
        }

        // TODO: call POST /api/auth/login
        console.log("Login with:", email);
    };

    return (
        <div style={{ maxWidth: "400px", margin: "80px auto", padding: "24px" }}>
            <h2>Login</h2>

            {error && <p style={{ color: "red" }}>{error}</p>}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "16px" }}>
                    <label htmlFor="email">Email</label><br />
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="you@example.com"
                        style={{ width: "100%", padding: "8px", marginTop: "4px" }}
                    />
                </div>

                <div style={{ marginBottom: "16px" }}>
                    <label htmlFor="password">Password</label><br />
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                        style={{ width: "100%", padding: "8px", marginTop: "4px" }}
                    />
                </div>

                <button type="submit" style={{ width: "100%", padding: "10px" }}>
                    Login
                </button>
            </form>

            <p style={{ marginTop: "16px" }}>
                Don&apos;t have an account? <Link to="/register">Register</Link>
            </p>
        </div>
    );
}

export default Login;
