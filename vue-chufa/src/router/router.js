import { createRouter, createWebHistory } from "vue-router";
import CreatePlanning from "@/views/planning/CreatePlanning.vue";
import PlanningTabs from "@/views/planning/PlanningPage.vue";
import Home from "@/views/Home.vue";
import PostDetail from "@/views/blog/PostDetail.vue";


const routes = [
    { path: "/", component: Home },
    { path: "/map", component: Map },
    { path: "/planningtabs", component: PlanningTabs },
    { path: "/createplanning", component: CreatePlanning },
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
        path: "/blog/bloghouse",
        name: "bloghouse",
        component: () => import("@/views/blog/bloghouse.vue"), // 指向 bloghouse 組件
    },
    {
        path: "/blog/bloghome",
        name: "bloghome",
        component: () => import("@/views/blog/bloghome.vue"), // 指向 bloghouse 組件
    },
    {
        path: "/admin/Role",
        name: "Role", // 路由名稱
        component: () => import("@/views/admin/Role.vue"), // 指向 Role 組件
    },
    {
        path: '/post/:id',
        name: 'PostDetail',
        component: PostDetail,
        props: true,
    },
    {
        path: "/blog/followblog",
        name: "followblog", // 路由名稱
        component: () => import("@/views/blog/followblog.vue"), // 指向 Role 組件
    }


]


export default createRouter({
    history: createWebHistory(),
    routes, // 傳遞定義的 routes
});