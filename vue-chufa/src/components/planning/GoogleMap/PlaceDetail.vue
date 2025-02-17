<template>
  <div v-if="place">
    <h2 style="padding-left: 20px">{{ place.placeName }}</h2>
    <div class="place-details">
      <!-- 照片區域 -->
      <div v-if="photos.length" class="photo-gallery-container">
        <div class="photo-gallery">
          <img
            v-for="(photo, index) in photos"
            :key="index"
            :src="getPhotoUrl(photo)"
            :alt="place.placeName"
            class="photo"
          />
        </div>
      </div>

      <!-- 文字資訊區 -->
      <div class="text-info-card">
        <p class="address">
          <i class="fas fa-map-marker-alt"></i> {{ place.placeAddress }}
        </p>
        <p v-if="place.rating" class="rating">
          <i class="fas fa-star"></i> 評分: {{ place.rating }}
        </p>
        <p v-if="place.placePhone" class="phone">
          <i class="fas fa-phone"></i> 電話: {{ place.placePhone }}
        </p>
        <p v-if="place.priceLevel" class="price">
          <i class="fas fa-dollar-sign"></i> 價位資訊: {{ place.priceLevel }}
        </p>

        <!-- 營業時間 -->
        <div v-if="formattedBusinessHours.length" class="business-hours">
          <p><i class="fas fa-clock"></i> 營業時間:</p>
          <ul>
            <li v-for="(item, index) in formattedBusinessHours" :key="index">
              {{ item.day }}: {{ item.hours }}
            </li>
          </ul>
        </div>

        <div class="links">
          <a v-if="place.website" :href="place.website" target="_blank" class="btn">
            <i class="fas fa-globe"></i> 官方網站
          </a>
          <a
            v-if="place.reservation"
            :href="place.reservation"
            target="_blank"
            class="btn reserve"
          >
            <i class="fas fa-calendar-check"></i> 訂位連結
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

// 接收 `place` 物件
const props = defineProps({
  place: {
    type: Object,
    default: () => ({}), // ✅ 確保 `place` 預設為空物件，避免 `null` 錯誤
  },
});

// ✅ 確保 `photos` 為陣列，避免 `null.length` 錯誤
const photos = computed(() =>
  Array.isArray(props.place?.photos) ? props.place.photos : []
);

// ✅ 格式化營業時間
const formattedBusinessHours = computed(() => {
  if (!props.place?.businessHours) return [];

  return props.place.businessHours.split("\n").map((line) => {
    const [day, hours] = line.split(": ");
    return { day: day.trim(), hours: hours ? hours.trim() : "未提供" };
  });
});

// 獲取照片 URL 的方法
const getPhotoUrl = (photo) => photo || "";
</script>

<style scoped>
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

.text-info-card {
  background: #fff;
  padding: 1.5rem;
  border-radius: 10px;
  max-width: 500px;
  font-family: "Arial", sans-serif;
}

p {
  margin: 0.5rem 0;
  color: #333;
}

.rating {
  color: #ff9800;
  font-weight: bold;
}

.phone,
.price {
  font-weight: bold;
}

.business-hours ul {
  padding-left: 1rem;
}

.links {
  margin-top: 1rem;
}

.btn {
  display: inline-block;
  background: #007bff;
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  text-decoration: none;
  margin-right: 0.5rem;
  transition: 0.3s;
}

.btn:hover {
  background: #0056b3;
}

.reserve {
  background: #28a745;
}

.reserve:hover {
  background: #1e7e34;
}

i {
  margin-right: 5px;
}
</style>
