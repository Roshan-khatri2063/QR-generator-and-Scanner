import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "../pages/Home.jsx";
import Login from "../pages/Login.jsx";
import Register from "../pages/Register.jsx";
import Scan from "../pages/Scan.jsx";
import Generator from "../pages/Generator.jsx";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/scan" element={<Scan />} />
                <Route path="/generator" element={<Generator />} />
            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;