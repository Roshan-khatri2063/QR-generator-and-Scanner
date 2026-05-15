import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";
import "./Navbar.css";

function Navbar() {
    const { user, isLoggedIn, logoutUser } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logoutUser();
        navigate("/login");
    };

    return (
        <nav className="navbar">
            <Link to="/" className="logo">QRHub</Link>

            <ul className="nav-links">
                <li><Link to="/">Home</Link></li>
                <li><Link to="/generate">Generate</Link></li>
                <li><Link to="/scan">Scan</Link></li>

                {isLoggedIn ? (
                    <>
                        <li style={{ color: "#fff", fontSize: "14px" }}>
                            Hi, <strong>{user.username}</strong>
                        </li>
                        <li>
                            <button
                                onClick={handleLogout}
                                style={{
                                    background: "transparent",
                                    border: "1px solid #fff",
                                    color: "#fff",
                                    padding: "6px 14px",
                                    borderRadius: "5px",
                                    cursor: "pointer",
                                    fontSize: "14px"
                                }}
                            >
                                Logout
                            </button>
                        </li>
                    </>
                ) : (
                    <>
                        <li><Link to="/login">Login</Link></li>
                        <li><Link to="/register">Register</Link></li>
                    </>
                )}
            </ul>
        </nav>
    );
}

export default Navbar;
