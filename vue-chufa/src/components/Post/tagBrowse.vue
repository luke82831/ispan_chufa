<template>
    <p class=tagBrowse>{{ tagData.tagName }}</p>
</template>
    
<script setup>
    const props = defineProps(['tagData'])
</script>
    
<style>
    .tagBrowse {
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
    .tagBrowse:hover {
        transform: scale(1.05);
    }
</style>