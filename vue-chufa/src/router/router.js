import { createRouter, createWebHashHistory } from "vue-router";

import Map from "@/views/Map.vue";
import { createWebHistory } from "vue-router";

const routes = [
    { path: "/map", component: Map },
];

export default createRouter({
    history: createWebHistory(),
    routes: routes,
});
