<template>
  <div v-if="place">
    <h2>{{ place.displayName }}</h2>
    <p>{{ place.formattedAddress }}</p>
    <p>經緯度: {{ place.location.lat }}, {{ place.location.lng }}</p>
    <p v-if="place.rating">評分: {{ place.rating }}</p>
    <p v-if="place.formattedPhoneNumber">
      電話: {{ place.formattedPhoneNumber }}
    </p>
    <p v-if="place.priceLevel">價位資訊: {{ place.priceLevel }}</p>
    <p v-if="place.openingHours">營業時間:</p>
    <ul v-if="place.openingHours">
      <li v-for="(time, index) in place.openingHours" :key="index">
        {{ time }}
      </li>
    </ul>
    <p v-if="place.website">
      網站: <a :href="place.website" target="_blank">{{ place.website }}</a>
    </p>
    <p v-if="place.url">
      地圖連結: <a :href="place.url" target="_blank"> 查看 Google 地圖</a>
    </p>
    <p v-if="place.photos && place.photos.length">照片:</p>
    <div v-if="place.photos && place.photos.length" class="photo-gallery">
      <img
        v-for="(photo, index) in place.photos"
        :key="index"
        :src="getPhotoUrl(photo)"
        :alt="place.displayName"
        class="photo"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  place: {
    type: Object,
    default: null,
  },
});

// 獲取照片 URL 的方法
const getPhotoUrl = (photo) => {
  try {
    return photo.getUrl({ maxWidth: 300, maxHeight: 300 });
  } catch (error) {
    console.error("無法獲取照片 URL:", error);
    return ""; // 返回空字串
  }
};
</script>

<style scoped>
.photo-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.photo {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
