<script setup>
import { RouterLink, RouterView } from 'vue-router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

</script>

<template>
  <div>
    <RouterLink to="/map">Map</RouterLink> | 
    <RouterLink to="/blog/create">BlogCreate</RouterLink>
  </div>
  <RouterView></RouterView>
</template>
<style>
  
</style>