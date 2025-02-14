<template>
    <div>
        <button @click="show = !show">切換顯示</button>
        <transition name="fade">
            <div v-if="show" class="box">Hello Vue!</div>
        </transition>
    </div>
</template>

<script>
export default {
    data() {
        return {
            show: true
        };
    }
};
</script>

<style scoped>
button {
    background-color: blue;
    color: white;
    margin: 10px;
    padding: 10px 20px;
    border: none;
    border-radius: 10px;
    cursor: pointer; /* 鼠標樣式：
        常規： pointer(手型), text(文本), not-allowed(禁止符號), wait(等待狀態), progress(處理進度) */
    transition: background-color 0.5s ease-in-out;
}

button:hover {
    background-color: red;
}

/* 定義過渡效果 */
.fade-enter-active, .fade-leave-active {
    transition: opacity 0.5s ease-in-out;
}
.fade-enter, .fade-leave-to {
    opacity: 0;
}

.box {
    width: 150px;
    height: 100px;
    background-color: lightblue;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 10px;
}
</style>
