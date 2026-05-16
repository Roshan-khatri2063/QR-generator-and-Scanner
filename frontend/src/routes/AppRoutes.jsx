import { BrowserRouter, Routes, Route } from "react-router-dom";

import Layout from "../component/Layout/Layout.jsx";
import Home from "../pages/Home.jsx";
import Login from "../pages/Login.jsx";
import Register from "../pages/Register.jsx";
import Scan from "../pages/Scan.jsx";
import Generator from "../pages/Generator.jsx";

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                {/* All routes share the Layout (Navbar + footer) */}
                <Route element={<Layout />}>
                    <Route path="/"          element={<Home />} />
                    <Route path="/generate"  element={<Generator />} />
                    <Route path="/scan"      element={<Scan />} />
                    <Route path="/login"     element={<Login />} />
                    <Route path="/register"  element={<Register />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;
