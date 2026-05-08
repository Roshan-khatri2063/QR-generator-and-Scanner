import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "../pages/Home";
import Login from "../pages/Login";
import Register from "../pages/Register";
import Scan from "../pages/Scan";
import Generator from "../pages/Generator";

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