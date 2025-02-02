import { createRouter, createWebHistory } from "vue-router";
import CreateItinerary from "@/views/planning/CreateItinerary.vue";
import PlanningPage from "@/views/planning/PlanningPage.vue";
import Home from "@/views/Home.vue";

const routes = [
    { path: "/", component: Home },
    { path: "/map", component: Map },
    { path: "/planningpage", component: PlanningPage },
    { path: "/createplanning", component: CreateItinerary },
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
    {
        path: "/blog/create",
        name: "BlogCreate",
        component: () => import("@/views/blog/blog.vue"), // 指向 post 組件
    },
    { path: "/map", component: Map },
]

export default createRouter({
    history: createWebHistory(),
    routes, // 傳遞定義的 routes
});