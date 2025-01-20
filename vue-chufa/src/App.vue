<template>
  <header></header>
  <div>
    <RouterLink to="/map">Map</RouterLink> |
    <!-- <RouterLink to="/createPlanning">開始規劃</RouterLink> | -->
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
  
</style>