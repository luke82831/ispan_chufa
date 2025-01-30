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

      <!-- 按鈕區域 -->
      <div class="button-container">
        <div class="action-buttons">
          <button @click="savePlace">儲存地點</button>
          <button @click="addToItinerary">加入行程</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import Swal from "sweetalert2";
import { usePlaceStore } from "@/stores/PlaceStore"; // 引入 Pinia store

const placeStore = usePlaceStore();

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

// 儲存地點
const savePlace = () => {
  if (!place.value) {
    console.error("Place is not defined");
    Swal.fire("地點資料未正確加載");
    return;
  }
  console.log("儲存地點:", place.value);
  Swal.fire({
    title: "已儲存景點",
    icon: "success",
    timer: 1500, // 設定訊息顯示時間為 1.5 秒
    showConfirmButton: false, // 隱藏確認按鈕
  });
};

let clickCount = 0; // 用來記錄點擊次數

// 加入行程
const addToItinerary = () => {
  if (!place.value) {
    console.error("Place is not defined");
    Swal.fire("地點資料未正確加載");
    return;
  }

  clickCount++; // 每次點擊地點時，clickCount 增加

  // 根據點擊次數來設定 origin 和 destination
  if (clickCount === 1) {
    // 第一次點擊，設置為 origin
    placeStore.setOrigin(place.value.location);
  } else if (clickCount === 2) {
    // 第二次點擊，設置為 destination
    placeStore.setDestination(place.value.location);
  } else {
    // 第三次以後，重新設置 origin 為上一個 destination，並設置新的 destination
    placeStore.setOrigin(place.value.location);
    clickCount = 1; // 重置點擊次數，為了讓下一個地點再次設置為 destination
  }

  // 呼叫 Pinia store 的方法來加入行程
  placeStore.addToItinerary(place.value);

  console.log("加入行程:", place.value);
};
</script>

<style scoped>
.place-details {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.text-info {
  flex: 1; /* 讓文字區域占據剩餘空間 */
  padding: 0px 20px 0px 20px;
}

.photo-gallery-container {
  display: flex;
  justify-content: center;
  width: 100%;
  overflow: hidden; /* 隱藏超出範圍的部分 */
}

.photo-gallery {
  display: flex;
  gap: 10px;
  overflow-x: auto; /* 啟用橫向滾動 */
  scroll-behavior: smooth; /* 使滾動平滑 */
}

.photo-gallery img {
  width: 100%; /* 設定圖片的寬度 */
  height: 300px;
  object-fit: cover;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.button-container {
  position: sticky;
  bottom: 0;
  background-color: white;
  padding: 10px;
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1); /* 按鈕區域陰影 */
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px; /* 控制按鈕之間的間距 */
}

.action-buttons button {
  padding: 15px 20px; /* 增加按鈕的內邊距，讓按鈕更大 */
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px; /* 增大字體 */
}

.action-buttons button:hover {
  background-color: #0056b3;
}
</style>
