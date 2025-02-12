<template>
    <div class="openTagsBox">
        <button v-if="!openTags" class="addTags" @click="openTagsAction">加入標籤</button> 
        <button v-if="openTags" class="addTags" @click="openTagsAction">關閉標籤</button>  
        <div v-if="optTags.length!=0" class="optTags">
            <p>已加入的標籤{{ optTags }}最多五個</p>
            <button @click="clear">清空</button>
        </div>
    </div>
    <div v-if="openTags" class="TagsBox">
        <div v-if="tagsData!=null" v-for="(tagData, key) in tagsData.data.list">
            <tag :tagData="tagData" :optTags="optTags" :optTagsId="optTagsId"></tag>
        </div>
    </div>
</template>
    
<script setup>
    import tag from './tag.vue';
    import { ref } from 'vue';
    import axiosapi from '@/plugins/axios.js';
    import eventBus from "@/eventBus";
    const props = defineProps(['optTagsId'])
    const tagsData = ref(null)
    const openTags = ref(false)
    const optTags = ref([])
    const openTagsAction = () => {
        openTags.value = !openTags.value
        if(openTags.value){
            findAllTags()
        }
    }
    const findAllTags = async () => {
        if(tagsData.value==null){
            console.log("抓標籤")
            const response = await axiosapi.get('/Tags/findAll');
            tagsData.value = response
            console.log(response)
        }else{
            console.log(tagsData.value)
        }
    }
    const clear = () => {
        optTags.value = []
        props.optTagsId.length = 0
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
    .TagsBox {
        display: flex;
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
    }
    .optTags{
        display: flex;
    }
</style>