<template>
    <div class="place-details">
      <div v-if="place">
        <h2>{{ place.placeName }}</h2>
        <p>{{ place.placeAddress }}</p>
        <p>
          <strong>城市：</strong>{{ place.city }}，
          <strong>區域：</strong>{{ place.region }}
        </p>
        <p v-if="place.placePhone"><strong>電話：</strong>{{ place.placePhone }}</p>
        <p v-if="place.businessHours">
          <strong>營業時間：</strong>{{ place.businessHours }}
        </p>
        <p v-if="place.placeInfo"><strong>地點資訊：</strong>{{ place.placeInfo }}</p>
        <p v-if="place.rating"><strong>評價：</strong>{{ place.rating }}</p>
        <p v-if="place.website">
          <strong>網站：</strong>
          <a :href="place.website" target="_blank">{{ place.website }}</a>
        </p>
        <p v-if="place.bookingUrl">
          <strong>預訂網址：</strong>
          <a :href="place.bookingUrl" target="_blank">{{ place.bookingUrl }}</a>
        </p>
  
        <div v-if="place.photos && place.photos.length">
          <h3>圖片</h3>
          <div class="photo-gallery">
            <img
              v-for="(photo, index) in place.photos"
              :key="index"
              :src="photo"
              alt="Place Photo"
            />
          </div>
        </div>
      </div>
      <p v-else>沒有選擇地點，請從地圖上選擇一個地點。</p>
    </div>
  </template>
  
  <script setup>
 defineProps({
  place: {
    type: Object,
    required: false,
  },
});

defineEmits(["update:selectedPlace"]);

// 假設某些事件或操作會觸發更新 selectedPlace
function updatePlace(newPlace) {
  // 當資料變更時，透過 emit 發送更新事件
  emit("update:selectedPlace", newPlace);
}
  </script>
  