import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Layout    from "../component/Layout/Layout.jsx";
import Home      from "../pages/Home.jsx";
import Login     from "../pages/Login.jsx";
import Register  from "../pages/Register.jsx";
import Scan      from "../pages/Scan.jsx";
import Generator from "../pages/Generator.jsx";
import { useAuth } from "../context/AuthContext.jsx";

/**
 * ProtectedRoute — wraps a route so only logged-in users can access it.
 * Redirects to /login if not authenticated.
 */
function ProtectedRoute({ children }) {
    const { isLoggedIn } = useAuth();
    return isLoggedIn ? children : <Navigate to="/login" replace />;
}

function AppRoutes() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<Layout />}>
                    {/* Public routes */}
                    <Route path="/"          element={<Home />} />
                    <Route path="/login"     element={<Login />} />
                    <Route path="/register"  element={<Register />} />

                    {/* Protected routes — require login */}
                    <Route path="/generate" element={
                        <ProtectedRoute><Generator /></ProtectedRoute>
                    } />
                    <Route path="/scan" element={
                        <ProtectedRoute><Scan /></ProtectedRoute>
                    } />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default AppRoutes;
