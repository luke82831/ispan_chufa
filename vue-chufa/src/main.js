import { createApp } from "vue";
import { createPinia } from "pinia";
import { useUserStore } from "@/stores/user.js";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import App from "./App.vue";
import router from "./router/router.js";
import "@fortawesome/fontawesome-free/css/all.min.css";

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);

const userStore = useUserStore();
userStore.fetchProfile().finally(() => {
  app.mount("#app");
});
