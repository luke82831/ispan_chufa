<template>
    <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" 
        @click="edit">
            <path d="M12 20h9" />
            <path d="M16.5 3.5a2.12 2.12 0 1 1 3 3L7 19l-4 1 1-4Z" />
        </svg>

        <div v-show="isClick">
            <button @click="editPost">編輯文章</button>
            <button @click="removePost">刪除文章</button>
        </div>
        <div v-if="isEdit">
            <blogEdit :postData="postData"></blogEdit>
        </div>
    </div>
</template>
    
<script setup>
    import blogEdit from './blogEdit.vue';
    import axiosapi from '@/plugins/axios.js';
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    const router = useRouter();

    const props = defineProps(['postData'])

    const isClick = ref(false)

    const edit = ()=>{
        console.log()
        if(isClick.value==false){
            isClick.value=true
        }else{
            isClick.value=false
            isEdit.value=false
        }
    }

    const isEdit = ref(false)
    const editPost = () => {
        console.log("編輯文章")
        if(isEdit.value==false){
            isEdit.value=true
        }else{
            isEdit.value=false
        }
    }

    const removePost = async () => {
        console.log("刪除文章") //後端要同時刪除全部留言，不然無法刪除
        const body = {"postid":props.postData.postid}
        await axiosapi.post(`/comment/deleteByPostId`,body);
        const response = await axiosapi.post(`/post/delete`,body);
        alert(response.data.message)
        console.log(response)
        if(response.data.successs){
            router.push("/");
        }else{
            alert(response.data.message)
        }
    }

</script>
    
<style>
    
</style>