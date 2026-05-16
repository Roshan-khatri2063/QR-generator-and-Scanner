import axios from "axios";

/**
 * Configured Axios instance for QRHub API calls.
 *
 * Day 4: Adds a request interceptor that automatically attaches
 * the JWT Bearer token to every outgoing request if one exists.
 *
 * The token getter is set from AuthContext after login via setTokenGetter().
 */
const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || "http://localhost:8086/backend_war_exploded",
    headers: {
        "Content-Type": "application/json",
    },
});

// Holds a reference to AuthContext's getToken function.
// Set once on app boot via setTokenGetter().
let tokenGetter = null;

/**
 * Called from AuthProvider to wire up the token getter.
 * @param {() => string | null} getter
 */
export function setTokenGetter(getter) {
    tokenGetter = getter;
}

// Request interceptor — attach Authorization header if token exists
api.interceptors.request.use(
    (config) => {
        const token = tokenGetter ? tokenGetter() : null;
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor — surface clean error messages
api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Attach a friendly message for components to display
        const message =
            error.response?.data?.error ||
            error.response?.data?.message ||
            "Something went wrong. Please try again.";
        error.friendlyMessage = message;
        return Promise.reject(error);
    }
);

export default api;
