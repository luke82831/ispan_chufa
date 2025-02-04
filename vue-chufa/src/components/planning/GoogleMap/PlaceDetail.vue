<template>
  <div v-if="place">
    <h2 style="padding-left: 20px">{{ place.displayName }}</h2>
    <div class="place-details">
      <!-- 照片區域 -->
      <div v-if="place.photos && place.photos.length" class="photo-gallery-container">
        <div class="photo-gallery">
          <img
            v-for="(photo, index) in place.photos"
            :key="index"
            :src="getPhotoUrl(photo)"
            :alt="place.displayName"
            class="photo"
          />
        </div>
      </div>

      <!-- 文字資訊區 -->
      <div class="text-info">
        <p>{{ place.formattedAddress }}</p>
        <p>經緯度: {{ place.location.lat }}, {{ place.location.lng }}</p>
        <p v-if="place.rating">評分: {{ place.rating }}</p>
        <p v-if="place.formattedPhoneNumber">電話: {{ place.formattedPhoneNumber }}</p>
        <p v-if="place.priceLevel">價位資訊: {{ place.priceLevel }}</p>
        <p v-if="place.openingHours">營業時間:</p>
        <ul v-if="place.openingHours">
          <li v-for="(hours, day) in place.openingHours" :key="day">
            {{ day }}: {{ hours }}
          </li>
        </ul>
        <p v-if="place.website">
          網站: <a :href="place.website" target="_blank">{{ place.website }}</a>
        </p>
        <p v-if="place.url">
          是否可訂位:
          <a :href="place.reservation" target="_blank">{{ place.reservation }}</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";

// 接收父組件傳遞的 place prop
const props = defineProps({
  place: {
    type: Object,
    default: () => null, // 預設為 null
  },
});

const place = ref(props.place);

watch(
  () => props.place,
  (newPlace) => {
    place.value = newPlace;
  }
);

// 獲取照片 URL 的方法
const getPhotoUrl = (photo) => {
  return photo; // 假設你有其他方法處理 URL
};
</script>

<style>
.place-details {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px); /* 減去 navbar 高度 */
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  overflow-y: auto; /* 讓內容可滾動 */
  position: relative;
  padding-bottom: 80px; /* 預留空間，避免內容被按鈕遮住 */
}

/* 讓內容區域不額外滾動 */
.text-info {
  padding: 20px;
}

/* 照片區塊 - 恢復原本的橫向排列 */
.photo-gallery-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 95%;
  padding: 10px;
  max-height: 340px; /* 增加高度，給滾動條留點空間 */
  overflow: hidden; /* 防止多餘空白 */
  position: relative; /* 讓內部的按鈕區域可以依附 */
}

/* 圖片橫向滾動區域 */
.photo-gallery {
  display: flex;
  gap: 10px;
  overflow-x: auto; /* 保持可滾動 */
  scroll-behavior: smooth;
  max-width: 100%;
  padding: 10px;
}

/* 自訂橫向滾動條 */
.photo-gallery::-webkit-scrollbar {
  height: 8px; /* 設定滾動條的高度 */
}

.photo-gallery::-webkit-scrollbar-thumb {
  background: linear-gradient(45deg, #007bff, #0056b3);
  border-radius: 10px;
}

.photo-gallery::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

/* 圖片樣式 */
.photo-gallery img {
  width: auto;
  height: 300px; /* 恢復你原本的圖片高度 */
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease-in-out;
}

.photo-gallery img:hover {
  transform: scale(1.05);
}
</style>
