import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8086/backend_war_exploded"
});

export default api;