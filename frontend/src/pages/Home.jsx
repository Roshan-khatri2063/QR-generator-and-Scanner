import { useEffect, useState } from "react";

import Layout from "../component/Layout/Layout.jsx";
import api from "../services/api.jsx";

function Home() {

    const [message, setMessage] = useState("");

    useEffect(() => {

        const fetchStatus = async () => {

            try {

                const response = await api.get("/api/status");

                setMessage(response.data.message);

            } catch (error) {

                console.log(error);

            }

        };

        void fetchStatus();

    }, []);

    return (

        <Layout>

            <div style={{ padding: "40px" }}>

                <h1>QR Generator System</h1>

                <p>{message}</p>

            </div>

        </Layout>

    );

}

export default Home;