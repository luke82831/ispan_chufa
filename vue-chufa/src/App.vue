<script setup>
import { RouterLink, RouterView } from 'vue-router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// Import PlanningForm
import PlanningForm from './components/planning/PlanningForm.vue';

</script>

<template>
  <header>
  </header>

    <div>
      <RouterLink to="/map">Map</RouterLink>
    </div>

  <!-- Include PlanningForm component -->
  <PlanningForm />


      <RouterView></RouterView>
      </template>
<style>
  
</style>