import { createRouter, createWebHistory } from "vue-router";
import CreateItinerary from "@/views/planning/CreateItinerary.vue";
import PlanningPage from "@/views/planning/PlanningPage.vue";
import Home from "@/views/Home.vue";
import AdminLayout from "@/layout/AdminLayout.vue";
import MemberManagement from "@/views/admin/MemberManagement.vue";
import PlaceManagement from "@/views/admin/PlaceManagement.vue";
import PostManagement from "@/views/admin/PostManagement.vue";
import PostDetail from "@/views/blog/PostDetail.vue";
import blogprofile from "@/views/blog/blogprofile.vue";
import followlist from "@/views/blog/followlist.vue";
import searchresult from "@/views/blog/searchresult.vue";


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
    path: "/blog/followlist/:id",
    name: "followlist",
    component: followlist,
    props: true,
  },
  {
    path: "/blog/create",
    name: "BlogCreate",
    component: () => import("@/views/blog/blogCreate.vue"), // 指向 post 組件
  },
  {
    path: "/blog/find/:postid",
    name: "BlogBrowse",
    component: () => import("@/views/blog/blogPostBrowse.vue"), // 指向 post 組件
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
  //路由
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
  },
  {
    path: '/blog/blogprofile/:bloghomeid',  // 動態路由
    name: 'blogprofile',
    component: blogprofile,  // 這是顯示用戶資料的頁面
    props: true,  // 這樣可以讓路由的參數傳遞給元件
  },
  {
    path: '/blog/followlist/:followid',  // 動態路由
    name: 'followlist',
    component: followlist,  // 這是顯示用戶資料的頁面
    props: true,  // 這樣可以讓路由的參數傳遞給元件
  },
  {
    path: '/search-results',
    name: 'SearchResults',
    component: searchresult,
    props: (route) => ({ query: route.query }), // 将查询参数作为 props 传递
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
