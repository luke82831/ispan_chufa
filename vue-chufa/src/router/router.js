import { createRouter, createWebHistory } from "vue-router";
import CreateItinerary from "@/views/planning/CreateItinerary.vue";
import PlanningPage from "@/views/planning/PlanningPage.vue";
import Home from "@/views/Home.vue";
import AdminLayout from "@/layout/AdminLayout.vue";
import MemberManagement from "@/views/admin/MemberManagement.vue";
import PlaceManagement from "@/views/admin/PlaceManagement.vue";
import PostManagement from "@/views/admin/PostManagement.vue";

const routes = [
  { path: "/", component: Home },
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
    path: "/blog/create",
    name: "BlogCreate",
    component: () => import("@/views/blog/blog.vue"), // 指向 post 組件
  },
  {
    path: "/admin",
    component: AdminLayout,
    children: [
      { path: "members", component: MemberManagement },
      { path: "places", component: PlaceManagement },
      { path: "post", component: PostManagement },
      { path: "", redirect: "/admin/members" }, // 預設顯示會員管理
    ],
  },
];

export default createRouter({
  history: createWebHistory(),
  routes, // 傳遞定義的 routes
});
