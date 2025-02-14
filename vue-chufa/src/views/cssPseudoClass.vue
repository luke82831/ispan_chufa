<template>
    <button class="button">點我</button>
    <div class="back">
        <div>
            <h1 class="hover">:hover(鼠標懸停)</h1>    
        </div>

        <div>
            <button class="focus">:focus(获得焦点)</button>
            <input class="focus">:focus(获得焦点)
        </div>

        <div>
            <p class="active">:active(元素被点击时)</p>
        </div>

        <div>
            <p class="firstChild">:first-child(第一个子元素)</p>
            <p class="firstChild">:first-child(第一个子元素)</p>
        </div>

        <div>
            <p class="nthChild">:nth-child(n)(第 n 个子元素)</p>
            <p class="nthChild">:nth-child(n)(第 n 个子元素)</p>
        </div>

        <div>
            <p class="not-a">:not(.a)(排除特定选择器)</p>
            <p class="not">:not(.a)(排除特定选择器)</p>
        </div>
    </div>
</template>
    
<script setup>
    
</script>
    
<style scoped>
    .button:hover {
    background-color: green;
    cursor: pointer; /* 鼠標樣式：
        常規： pointer(手型), text(文本), not-allowed(禁止符號), wait(等待狀態), progress(處理進度) */
    }

    .button:active {
    background-color: darkgreen;
    cursor: wait;
    }

    .button:focus {
    outline: 3px solid orange;
    }
    .back{
        background-color: rgb(177, 177, 177);
    }
    .hover:hover{
        background-color: red;
    }
    .focus:focus{
        background-color: rgb(255, 153, 0);
    }
    .active:active{
        background-color: rgb(251, 255, 0);
    }
    .firstChild:first-child{
        color: rgb(145, 255, 0);
    }
    .nthChild:nth-child(2){
        color: rgb(0, 255, 157);
    }
    .not:not(.a) {
        color: rgb(0, 119, 255);
    }
</style>