import { createRouter, createWebHistory } from "vue-router";
import Map from "@/views/Map.vue";
// import CreatePlanning from "@/views/planning/CreatePlanning.vue";

const routes = [
    {
        path: "/secure/Login",
        name: "Login",
        component: () => import("@/views/secure/Login.vue"), // 指向 Login 組件
    },
    {
        path: "/secure/Profile",
        name: "Profile",
        component: () => import("@/views/secure/Profile.vue"), // 指向 Profile 組件
    },
    {
        path: "/secure/Register",
        name: "Register",
        component: () => import("@/views/secure/Register.vue"), // 指向 Register 組件
    },
    {
        path: "/admin/Role",
        name: "Role", // 路由名稱
        component: () => import("@/views/admin/Role.vue"), // 指向 Role 組件
    },

    { path: "/map", component: Map },
    // { path: "/createPlanning", component: CreatePlanning },


]


export default createRouter({
    history: createWebHistory(),
    routes, // 傳遞定義的 routes
});