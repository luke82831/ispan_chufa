<template>
    <div class="openTagsBox">
        <button @click="editTagsOk" class="inputEditTags">確認編輯</button>
        <div v-if="optTags.length!=0" class="optTags">
            <button @click="clear" class="recallEditTags">清空</button>
            <p>已加入的標籤{{ optTags }}最多五個</p>
        </div>
    </div>
    <div class="editTagsBox">
        <div v-if="tagsData!=null" v-for="tagData in tagsData.data.list">
            <editTag :tagData="tagData" :optTags="optTags" :optTagsId="optTagsId"></editTag>
        </div>
        <div>
            <newTag :findAllTags="findAllTags"></newTag>
        </div>
    </div>
</template>
    
<script setup>
    import newTag from '../PostCreate/newTag.vue';
    import editTag from './editTag.vue';
    import { ref, onMounted } from 'vue';
    import axiosapi from '@/plugins/axios.js';
    import eventBus from "@/eventBus";
    const props = defineProps(['tags','postData'])
    const tagsData = ref(null)
    const optTagsId = ref([])
    const optTags = ref([])
    onMounted(()=>{
        findAllTags()
            for(let i=0;i<props.tags.length;i++){
                optTags.value.push(props.tags[i].tagName)
                optTagsId.value.push(props.tags[i].tagId)
            }
    })
    const editTagsOk = async () => {
        console.log("更新貼文標籤")
        const body = {
            "postid":props.postData.postid,
            "tagId":optTagsId.value,
        }
        const response = await axiosapi.put(`/post/updateTags`,body);
        
        alert(response.data.message);
        if(response.data.successs){
            console.log(response.data)
            window.location.reload();
        }

    }
    const findAllTags = async () => {
        console.log("抓標籤")
        const response = await axiosapi.get('/Tags/findAll');
        tagsData.value = response
        console.log(response)
    }
    const clear = () => {
        optTags.value.length = 0
        optTagsId.value.length = 0
        eventBus.emit('clearOptTags');
    }
</script>
    
<style>
    .addTags{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #17a2b8;
        color: white;
        margin-bottom: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .addTags:hover {
        transform: scale(1.05);
    }
    .editTagsBox {
        display: flex;
        flex-wrap: wrap;
        width: 400px;
    }
    .tag {
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #2564b6;
        color: white;
        margin: 10px;
        margin-top: 0px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .tag:hover {
        transform: scale(1.05);
    }
    .openTagsBox{
        display: flex;
        flex-wrap: wrap;
        width: 400px;
    }
    .optTags{
        display: flex;
    }
    .recallEditTags{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: rgb(226, 66, 66);
        color: white;
        margin: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .recallEditTags:hover {
        transform: scale(1.05);
    }
    .inputEditTags{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: rgb(28, 119, 155);
        color: white;
        margin: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }.inputEditTags:hover {
        transform: scale(1.05);
    }
</style>