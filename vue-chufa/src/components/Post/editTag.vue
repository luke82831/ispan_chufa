<template>
    <p :class=tagClass @click="optTag(tagData)">{{ tagData.tagName }}</p>
</template>
    
<script setup>
    import { ref, onMounted } from 'vue';
    import eventBus from "@/eventBus";
    const tagClass = ref('tag')
    const props = defineProps(['tagData','optTags','optTagsId'])
    const optTag = (tagData) => {
        const tagName = tagData.tagName
        const tagId = tagData.tagId

        if(props.optTags.indexOf(tagName) == -1){
            if(props.optTags.length!=5){
                console.log(`${tagName}加入標籤`)
                props.optTags.push(tagName)
                props.optTagsId.push(tagId)
                tagClass.value = "optTagIn"
            }else{
                alert("標籤最多五個")
            }
        }else{
            console.log(`${tagName}取消標籤`)
            props.optTags.splice(props.optTags.indexOf(tagName), 1)
            props.optTagsId.splice(props.optTagsId.indexOf(tagId), 1)
            tagClass.value = "tag"
        }
    }

    onMounted(()=>{
        InitialClass()
        eventBus.on("clearOptTags", () => {
            tagClass.value = "tag"
        });
    })
    const InitialClass = () => {
        for(let i=0;i<props.optTags.length;i++){
            if(props.optTags[i] == props.tagData.tagName){
                tagClass.value = "optTagIn"
            }
        }
    }
</script>
    
<style>
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
    .optTagIn{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #b62a25;
        color: white;
        margin: 10px;
        margin-top: 0px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
</style>