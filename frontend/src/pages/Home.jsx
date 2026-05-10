import { useEffect, useState } from "react";
import api from "../services/api.jsx";

function Home() {
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const response = await api.get("/api/status");
                setMessage(response.data.message);
            } catch (err) {
                setError("Could not connect to backend.");
                console.error(err);
            }
        };

        void fetchStatus();
    }, []);

    return (
        <div style={{ padding: "40px" }}>
            <h1>QR Generator System</h1>
            {message && <p style={{ color: "green" }}>{message}</p>}
            {error   && <p style={{ color: "red"   }}>{error}</p>}
        </div>
    );
}

export default Home;
