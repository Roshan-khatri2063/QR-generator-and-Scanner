import { Link } from "react-router-dom";
import "./Navbar.css";

function Navbar() {
    return (
        <nav className="navbar">
            <h2 className="logo">QRHub</h2>

            <ul className="nav-links">
                <li><Link to="/">Home</Link></li>
                <li><Link to="/generate">Generate</Link></li>
                <li><Link to="/scan">Scan</Link></li>
                <li><Link to="/login">Login</Link></li>
                <li><Link to="/register">Register</Link></li>
            </ul>
        </nav>
    );
}

export default Navbar;
