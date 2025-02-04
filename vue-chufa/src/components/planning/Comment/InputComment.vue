<template>
    <div>
        <h1>新增留言</h1>
        <input type="text" placeholder="請輸入留言" v-model="comment" >
        <button @click="outputComment">送出</button>
        <button @click="removeComment">取消</button>
    </div>
</template>
    
<script setup>
    import axiosapi from '@/plugins/axios.js';
    import { ref } from 'vue';
    import { useRoute } from 'vue-router';

    import eventBus from '@/eventBus';

    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數

    const comment=ref('')
    const outputComment = async () => {
        const body = {
            "postId":postid.value,
            "userId":"1",
            "content":comment.value,
            "parentId":""
        }
        const response =await axiosapi.post(`/comment/create`,body);
        if (response.data.successs) {
            console.log(response.data)
        }else{
            alert(response.data.message);
        }
        eventBus.emit('outputComment','創建完畢');
        comment.value=''
    }
    const removeComment = () => {
        comment.value=''
    }

</script>
    
<style>
    
</style>