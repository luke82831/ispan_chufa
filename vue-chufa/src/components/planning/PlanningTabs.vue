<template>
<div class="container">
    <!-- 左邊區塊: 表單 -->
    <div class="form-container">
    <PlanningForm @submit="handleFormSubmit"></PlanningForm>
    </div>

    <!-- 右邊區塊: 顯示地圖或其他內容 -->
    <div class="map-container">
    <router-view></router-view>
    </div>
</div>
</template>

<script>

</script>

<style scoped>
.container {
display: flex;
justify-content: space-between;
gap: 2rem;
}

.form-container {
flex: 1;
padding: 1rem;
border: 1px solid #ccc;
border-radius: 8px;
}

.map-container {
flex: 2;
padding: 1rem;
border: 1px solid #ccc;
border-radius: 8px;
}
</style>