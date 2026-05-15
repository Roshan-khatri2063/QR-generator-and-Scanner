import { createContext, useContext, useState } from "react";

/**
 * AuthContext — provides user auth state to the whole app.
 *
 * Exposes:
 *  - user        : { id, username, email } or null
 *  - isLoggedIn  : boolean
 *  - loginUser   : (userData) => void
 *  - logoutUser  : () => void
 *
 * JWT will be stored here in Day 4. For now we store user info in memory.
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);

    const loginUser = (userData) => {
        setUser(userData);
    };

    const logoutUser = () => {
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{
            user,
            isLoggedIn: user !== null,
            loginUser,
            logoutUser,
        }}>
            {children}
        </AuthContext.Provider>
    );
}

/**
 * Custom hook — use inside any component to access auth state.
 * Example: const { user, isLoggedIn, logoutUser } = useAuth();
 */
export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
    return ctx;
}
