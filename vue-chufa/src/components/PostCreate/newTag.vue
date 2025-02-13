<template>
    <p class="newTag" @click="createNewTag">+</p>
    <div v-if="openNewTag" class="newTagBox">
        <input class="inputNewTag" type="text" placeholder="新標籤名稱" v-model="inputNewTag">
        <button class="buttonNewTag" @click="addNewTag">確認</button>
    </div>
</template>
    
<script setup>
    import { ref } from 'vue';
    import axiosapi from '@/plugins/axios';
    const props = defineProps(['findAllTags'])
    const openNewTag = ref(false)
    const inputNewTag = ref('')
    const createNewTag = () => {
        openNewTag.value = !openNewTag.value
    }
    const addNewTag = async () => {
        console.log("創建新標籤")
        const body = {"tagName":inputNewTag.value}
        const response = await axiosapi.post('/Tags/create',body);
        console.log(response.data)
        if(response.data.successs){
            props.findAllTags()
        }else{
            alert(response.data.message)
        }
        openNewTag.value = !openNewTag.value
    }
</script>
    
<style>
    .newTag {
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #8fbdf8;
        color: white;
        margin: 10px;
        margin-top: 0px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .newTag:hover {
        transform: scale(1.05);
    }
    .inputNewTag{
        padding: 10px 20px;
        background-color: #9bc3f7;
        font-size: 14px;
        border: none;
        border-radius: 25px 0 0 25px;
        width: 80px;
    }
    .buttonNewTag{
        padding: 10px 20px;
        background-color: #76acf3;
        font-size: 14px;
        border: none;
        border-radius: 0 25px 25px 0;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }
    .buttonNewTag:hover {
        background-color: rgb(72, 162, 223)
    }
    .newTagBox{
        display: flex;
        position: absolute;
    }
</style>