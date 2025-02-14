<template>
    <div class="container">
        <div class="item">
            <p>00</p>
            <p>01</p>
        </div>
        <div class="item" style="align-self: auto;">
            <p>00</p>
            <p>02</p>
        </div>
        <div class="item" style="align-self: flex-end;">
            <p>00</p>
            <p>03</p>
        </div>
        <div><p>0123456789</p></div>
    </div>
</template>
    
<script setup>
    
</script>
    
<style scoped>
    .item{
        /* 文字屬性: */
        color: rgb(184, 181, 181); /* 顏色 */
        font-size: 40pt; /* 大小 */
        font-weight: bold; /* 粗細 */
        font-family: Brush Script MT; /* 字體 */
        text-align: center; /* 對齊 */
        text-decoration: underline; /* 裝飾 */
        letter-spacing: 10px; /* 間距 */
        line-height: 100px; /* 行高 */
        
        /* 佈局與盒模型 */
        width: 100px; /* 內容區(寬度) */
        height: 200px; /* 內容區(高度) */
        margin: 10px; /* 外邊距 */
        padding: 20px; /* 內邊距 */
        border: 10px solid rgb(197, 183, 183); /* 邊框 */
        display: flex; /* 顯示方式 */
        position: sticky; /* 定位方式 */
            /* 偏移 */
            top: 10px; /* 由上往下偏移 */
            left: 0px; /* 由左往右偏移 */
            bottom: 10px; /* 由下往上偏移 */
            /* right: 0px; 由右往左偏移 */
        
        /* 背景與邊框 */
        background-color: black; /* 背景顏色 */
        background-image: url('src/assets/0114-3.jpg'); /* 背景圖片 */
        background-size: cover; /* 顯示背景圖片比例 */
        background-position: bottom; /* 顯示背景圖片位置 */
        border-radius: 50px; /* 邊框圓角 */
        box-shadow:  5px 5px 10px gray; /* 盒子陰影 */

        /* Flexbox佈局(項目屬性) */
        order: 1; /* 排列顺序 */
        flex: 1  1 200px; /* flex-grow, flex-shrink 和 flex-basis的简写 */
        /* flex-grow: 1; 项目的放大比例 */
        /* flex-shrink: 1; 项目的縮小比例 */
        /* flex-basis: 200px; 项目的初始大小 */
        align-self: flex-start; /* 交叉軸對齊 */
    }

    .container { /* 父容器(Flex 容器) */
        height: 1000px;
        /* width: 800px; */
        background-color: rgb(231, 231, 231); 
        
        /* Flexbox佈局(容器屬性) */
        display: flex; /* 啟用Flexbox佈局 */
        flex-direction: row; /* 排列方向： 
            row（默认值）(左到右)
            row-reverse(右到左)
            column(上到下)
            column-reverse(下到上) */
        justify-content: space-around; /* 主軸對齊:
            flex-start（默认值）(向容器的起始位置对齐)
            flex-end(向容器的结束位置对齐)
            center(居中对齐)
            space-between(项目之间均匀分布，第一个项目对齐起始位置，最后一个项目对齐结束位置)
            space-around(项目之间均匀分布，每个项目两侧有相等的间距)
        */
        align-items: center; /* 交叉軸對齊 */
        /* flex-wrap: wrap; */
    }
</style>