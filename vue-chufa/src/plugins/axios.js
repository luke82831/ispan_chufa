import axios from "axios";

const axiosapi = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials: true,
});

// Request Interceptor: 自動添加 Authorization Header
axiosapi.interceptors.request.use(
    function (config) {
        // 從 localStorage 或其他地方獲取 Token
        const token = localStorage.getItem("token"); // 假設 Token 存在於 localStorage
        console.log("Token in Request Interceptor:", token); // 打印 Token
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // 添加 Authorization Header
        }
        return config;
    },
    function (error) {
        return Promise.reject(error);
    }
);

// Response Interceptor: 處理 403 錯誤
axiosapi.interceptors.response.use(
    function (response) {
        return response;
    },
    function (error) {
        if (error.response && error.response.status && error.response.status === 403) {
            window.location.href = "/403"; // 重定向到 403 錯誤頁面
        }
        return Promise.reject(error);
    }
);

export default axiosapi;
