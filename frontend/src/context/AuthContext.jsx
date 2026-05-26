/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect, useCallback } from "react";
import { setTokenGetter } from "../services/api.jsx";

/**
 * AuthContext — global auth state for QRHub.
 *
 * Day 4 additions:
 *  - Stores JWT token in memory (never localStorage for security)
 *  - Parses token expiry and auto-logs-out when it expires
 *  - Exposes getToken() for use in Axios interceptor
 *
 * Exposes:
 *  user        : { id, username, email } | null
 *  token       : JWT string | null
 *  isLoggedIn  : boolean
 *  loginUser   : (userData, token) => void
 *  logoutUser  : () => void
 *  getToken    : () => string | null
 */
const AuthContext = createContext(null);

/** Decode JWT payload without verifying signature (client-side only). */
function parseJwtPayload(token) {
    try {
        const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        return JSON.parse(atob(base64));
    } catch {
        return null;
    }
}

export function AuthProvider({ children }) {
    const [user,  setUser]  = useState(null);
    const [token, setToken] = useState(null);

    /**
     * Called after a successful login API response.
     * @param {{ id, username, email }} userData
     * @param {string} jwtToken
     */
    const loginUser = useCallback((userData, jwtToken) => {
        setUser(userData);
        setToken(jwtToken);
    }, []);

    const logoutUser = useCallback(() => {
        setUser(null);
        setToken(null);
    }, []);

    /** Returns the current JWT (used by Axios interceptor). */
    const getToken = useCallback(() => token, [token]);

    // Make the latest token available to the Axios interceptor
    useEffect(() => {
        setTokenGetter(getToken);
        return () => setTokenGetter(null);
    }, [getToken]);

    // Auto-logout when the JWT expires
    useEffect(() => {
        if (!token) return;

        const payload = parseJwtPayload(token);
        if (!payload?.exp) return;

        const msUntilExpiry = payload.exp * 1000 - Date.now();
        if (msUntilExpiry <= 0) {
            const immediate = setTimeout(() => {
                logoutUser();
            }, 0);
            return () => clearTimeout(immediate);
        }

        const timer = setTimeout(() => {
            logoutUser();
            alert("Your session has expired. Please log in again.");
        }, msUntilExpiry);

        return () => clearTimeout(timer);
    }, [token, logoutUser]);

    return (
        <AuthContext.Provider value={{
            user,
            token,
            isLoggedIn: user !== null,
            loginUser,
            logoutUser,
            getToken,
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
    return ctx;
}
