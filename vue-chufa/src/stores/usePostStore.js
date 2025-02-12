import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePostStore = defineStore('post', () => {
    // 存放被隱藏的文章資訊 { postid: reason }
    const hiddenPosts = ref(JSON.parse(localStorage.getItem('hiddenPosts')) || {});


    // **新增移除隱藏狀態的方法**
    const removeHiddenPost = (postid) => {
        delete hiddenPosts.value[postid];
        localStorage.setItem('hiddenPosts', JSON.stringify(hiddenPosts.value));
    };


    // 設定某篇文章為隱藏
    const setHiddenPost = (postid, reason) => {
        hiddenPosts.value[postid] = reason;
        localStorage.setItem('hiddenPosts', JSON.stringify(hiddenPosts.value));
    };

    // 取得某篇文章的隱藏原因
    const getHiddenReason = (postid) => {
        return hiddenPosts.value[postid] || null;
    };

    return { hiddenPosts, setHiddenPost, removeHiddenPost, getHiddenReason };
});


