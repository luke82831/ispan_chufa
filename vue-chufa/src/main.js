import { createApp } from "vue";
import { createPinia } from "pinia";
import { useUserStore } from "@/stores/user.js";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import App from "./App.vue";
import router from "./router/router.js";
import "@fortawesome/fontawesome-free/css/all.min.css";

// import { createVuetify } from 'vuetify';
// import 'vuetify/styles'; // 引入 Vuetify 樣式
// import * as components from 'vuetify/components';


// const vuetify = createVuetify({
//   components,
// });

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);
// app.use(vuetify);

const userStore = useUserStore();
userStore.fetchProfile().finally(() => {
  app.mount("#app"); // 等待初始化後再掛載應用
});
