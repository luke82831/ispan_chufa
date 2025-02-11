<template>
    <div>
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" 
        @click="isClick = !isClick ; isEdit = false" id="openEdit" class="openEdit">
            <path d="M12 20h9" />
            <path d="M16.5 3.5a2.12 2.12 0 1 1 3 3L7 19l-4 1 1-4Z" />
        </svg>

        <div v-if="isClick" class="editBox">
            <button class="editButton edit" @click="isEdit = !isEdit">編輯文章</button>
            <button v-if="!isEdit" class="editButton remove" @click="removePost">刪除文章</button>
            <div v-if="isEdit">
                <blogEdit :postData="postData"></blogEdit>
            </div>
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

    const isEdit = ref(false)

    const openEditTop = ref(0)
    const openEditLeft = ref(0)

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
    
<style scoped>
    @keyframes fadeIn {
        from {
            opacity: 0; /* 初始透明 */
        }
        to {
            opacity: 1; /* 完全显示 */
        }
    }
    .openEdit:hover{
        cursor: pointer;
        color: rgb(219, 35, 3);
    }
    .editBox{
        position: absolute;
        background: linear-gradient(145deg, #ffffff, #f8f8f8);
        border: 1px solid #ddd;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        
        animation: fadeIn 0.3s ease;

        display: flex;
        flex-direction: column;
    }
    .editButton{
        color: #4882c0;
        padding: 10px;
        width: 140px;
        text-align: left;
        font-size: 16px;
        background-color: #ffffff;
        border: none;
    }
    .edit{
        margin-top: 10px;
        margin-bottom: 2px;
    }
    .remove{
        color: red;
        margin-top: 2px;
        margin-bottom: 10px;
    }
    .editButton:hover{
        background-color: #d4f3f3
    }
</style>