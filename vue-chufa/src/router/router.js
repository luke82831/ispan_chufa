import { createRouter, createWebHistory } from "vue-router";
import MyItineraries from "@/views/planning/MyItineraries.vue";
import ItineraryForm from "@/components/planning/Itinerary/ItineraryForm.vue";
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
import { useUserStore } from "@/stores/user";
import Swal from "sweetalert2";
import { ref } from "vue";

const routes = [
  {
    path: "/css",
    name: "css",
    component: () => import("@/views/css.vue"),
  },
  //主頁
  { path: "/", 
    name:"Home",
    component: Home },
  //會員系統
  {
    path: "/secure/Login",
    name: "Login",
    component: () => import("@/views/secure/Login.vue"),
  },
  {
    path: "/secure/Profile",
    name: "Profile",
    component: () => import("@/views/secure/Profile.vue"),
  },
  {
    path: "/secure/Register",
    name: "Register",
    component: () => import("@/views/secure/Register.vue"),
  },
  //行程規劃
  {
    path: "/myitineraries",
    component: MyItineraries,
    meta: { requiresAuth: true }, //需要登入才可使用
  },
  {
    path: "/itineraryform",
    component: ItineraryForm,
    meta: { requiresAuth: true }, //需要登入才可使用
  },
  {
    path: "/planningpage/:tripId",
    component: PlanningPage,
    meta: { requiresAuth: true }, //需要登入才可使用
  },
  //部落格系統
  {
    path: "/blog/followlist/:id",
    name: "followlist",
    component: followlist,
    props: true,
  },
  {
    path: "/blog/bloghouse",
    name: "bloghouse",
    component: () => import("@/views/blog/bloghouse.vue"),
  },
  {
    path: "/blog/bloghome",
    name: "bloghome",
    component: () => import("@/views/blog/bloghome.vue"),
  },
  {
    path: "/post/:id",
    name: "PostDetail",
    component: PostDetail,
    props: true,
  },
  {
    path: "/blog/followblog",
    name: "followblog",
    component: () => import("@/views/blog/followblog.vue"),
  },
  {
    path: "/blog/blogprofile/:bloghomeid",
    name: "blogprofile",
    component: blogprofile,
    props: true,
  },
  {
    path: "/blog/followlist/:followid",
    name: "followlist",
    component: followlist, // 這是顯示用戶資料的頁面
    props: true,
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
    meta: { requiresAuth: true }, //需要登入才可使用
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
    path: "/search-results",
    name: "SearchResults",
    component: searchresult,
    props: (route) => ({ query: route.query }), // 将查询参数作为 props 传递
  },
  //管理後臺
  {
    path: "/admin",
    component: AdminLayout,
    children: [
      { path: "members", component: MemberManagement },
      { path: "places", component: PlaceManagement },
      { path: "post", component: PostManagement },
      { path: "", redirect: "/admin/members" }, // 預設顯示會員管理
    ],
    meta: { requiresAuth: true }, //需要登入才可使用
  },
];

// export default createRouter({
//   history: createWebHistory(),
//   routes, // 傳遞定義的 routes
// });

// 創建 Router 實例
const router = createRouter({
  history: createWebHistory(),
  routes,
});

export const isSearch = ref(false);
router.afterEach((to, from) => {
  // 這裡可以根據路徑進行條件判斷
  if (to.path === '/') {
    // 假設您有一個全局變數 isSearch，可以在這裡將其設置為 false
    isSearch.value = false;
  }
});

// **全域前置守衛**
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // **如果會員資料尚未載入，則執行 fetchProfile()**
  if (!userStore.profileLoaded) {
    await userStore.fetchProfile();
  }

  // **處理「需要登入」的頁面**
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    Swal.fire("未登入", "請先登入後再繼續", "warning");
    next("/secure/Login"); // 重定向到登入頁面
  }
  // **處理「可登入也可不登入」的頁面**
  else {
    next(); // 放行，讓未登入使用者也能進入
  }
});

export default router;