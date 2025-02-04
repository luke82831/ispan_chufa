<template>
    <div>
        <p class="title">{{ title }}</p>
        <p v-show="title!=null">==================================</p>
        <div class="container">
            <div id="container"></div>
        </div>
    </div>
    <inputComment></inputComment>
    <rootComment></rootComment>
</template>
    
<script setup>
    import inputComment from "@/components/planning/Comment/inputComment.vue";
    import rootComment from "@/components/planning/Comment/rootComment.vue";
    import axiosapi from '@/plugins/axios.js';
    import { ref, onMounted } from 'vue';
    import { useRoute } from 'vue-router';

    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數

    const title=ref('');
    const findPost=async ()=>{
        const response =await axiosapi.get(`/post/findById/${postid.value}`);
        if (response.data.successs) {
            title.value=response.data.list[0].postTitle
            container.innerHTML = response.data.list[0].postContent
        }else{
            alert(response.data.message);
        }
    }
    onMounted(()=>{
        findPost()
    })
</script>
    
<style>
    .title {
        font-size: 50px;
        margin: 20px;
    }
</style>