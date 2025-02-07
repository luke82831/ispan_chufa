<template>
    <div class="container">
        <div class="item">1</div>
        <div class="item">2</div>
        <div class="item">3</div>
    </div>
</template>
    
<script setup>
    
</script>
    
<style>
    .container { /* 父容器(Flex 容器) */
        display: flex; /* 使用Flex布局 */
        background-color: lightgray; /* 背景顏色 */
        flex-direction: column-reverse; /* item(Flex 项目)排列方向
        row | row-reverse |
        column | column-reverse; */
    }
</style>