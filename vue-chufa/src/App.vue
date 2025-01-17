<template>
  <header></header>
  <div>
    <RouterLink to="/">Chufa首頁</RouterLink> |
    <RouterLink to="/createPlanning" id="planningbutton">開始規劃</RouterLink>
    <RouterLink to="/secure/Login">登入功能</RouterLink> |
    <RouterLink to="/secure/Profile">會員資料</RouterLink> |
    <RouterLink to="/secure/Register">註冊功能</RouterLink> |
  </div>
    <RouterView></RouterView>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

</script>

<style>
#planningbutton {
  position: fixed;
  bottom: 50px;
  right: 50px;
  width: 100px; /* 按鈕直徑 */
  height: 100px; /* 按鈕直徑 */
  background-color: #84baf5; /* 按鈕顏色 */
  color: #fff;
  border-radius: 50%; /* 圓形按鈕 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  padding: 15px; /* 內間距，用於讓文字有空間 */
  font-size: 30px; /* 調整文字大小 */
  font-weight: bold;
  text-decoration: none; /* 移除超連結的下劃線 */
  cursor: pointer;
  z-index: 1000; /* 確保按鈕在最上層 */
  line-height: 1.2; /* 行距，控制換行後的間距 */
  white-space: normal; /* 允許文字換行 */
  overflow-wrap: break-word; /* 防止文字溢出 */
  transition: transform 0.2s, background-color 0.2s;
}

#planningbutton:hover {
  transform: scale(1.1);
  background-color: #5a95d5; /* 深藍色 */
}

</style>