<template>
  <header></header>
  <div class="router">
    <RouterLink to="/">Chufa首頁</RouterLink> |
    <RouterLink to="/createPlanning" id="planningbutton">開始規劃</RouterLink>
    <RouterLink to="/secure/Login">登入功能</RouterLink> |
    <RouterLink to="/secure/Profile">會員資料</RouterLink> |
    <RouterLink to="/secure/Register">註冊功能</RouterLink> |
  </div>
  <RouterView></RouterView>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router";
</script>

<style>
#planningbutton {
  position: fixed;
  bottom: 50px;
  right: 50px;
  width: 100px;
  height: 100px;
  background-color: #84baf5;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  font-size: 35px; /* 根據需要調整字體大小 */
  font-weight: bold;
  text-decoration: none;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center; /* 使文字在圓形按鈕中垂直與水平居中 */
  white-space: normal; /* 允許文字換行 */
  overflow-wrap: break-word; /* 防止文字溢出圓形範圍 */
  padding: 10px; /* 內邊距，讓文字不會貼到圓形的邊緣 */
  transition: transform 0.2s, background-color 0.2s;
}

#planningbutton:hover {
  transform: scale(1.1);
  background-color: #5a95d5; /* 深藍色 */
}
</style>
