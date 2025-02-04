import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import App from './App.vue';
import router from './router/router.js';
import { useUserStore } from '@/stores/user.js';
// 引入 Font Awesome 的 CSS
import '@fortawesome/fontawesome-free/css/all.min.css';

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);

const userStore = useUserStore();
userStore.fetchProfile().finally(() => {
    app.mount('#app'); // 等待初始化後再掛載應用
});
