<template>
  <div v-if="place">
    <h2 style="padding-left: 20px">{{ place.displayName }}</h2>
    <div class="place-details">
      <!-- 照片區域 -->
      <div
        v-if="place.photos && place.photos.length"
        class="photo-gallery-container"
      >
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
        <p v-if="place.formattedPhoneNumber">
          電話: {{ place.formattedPhoneNumber }}
        </p>
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
          <a :href="place.reservation" target="_blank">{{
            place.reservation
          }}</a>
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
import { usePlaceStore } from "@/stores/PlaceStore";
import { useItineraryStore } from "@/stores/ItineraryStore";

const placeStore = usePlaceStore();
const itineraryStore = useItineraryStore();

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

// 加入行程
const addToItinerary = () => {
  if (!place.value) {
    console.error("Place is not defined");
    Swal.fire("地點資料未正確加載");
    return;
  }

  // 取得當前選擇的行程日期
  const selectedDate = itineraryStore.selectedDate;
  if (!selectedDate) {
    Swal.fire("請先選擇行程日期");
    return;
  }

  // 取得當前行程的地點列表
  const itineraryForSelectedDay =
    itineraryStore.getItineraryForDay(selectedDate);

  // 找到新地點的索引位置
  const newIndex = itineraryForSelectedDay.length;

  // 呼叫 Pinia store 的方法來加入行程
  itineraryStore.addPlaceToDay(selectedDate, place.value);

  console.log(
    `📌 新增行程地點: ${place.value.displayName} (索引: ${newIndex})`
  );

  // **自動更新 `origin` 和 `destination`**
  if (newIndex > 0) {
    // 取得上一個地點作為新的起點
    const previousPlace = itineraryForSelectedDay[newIndex - 1].location;
    const newPlaceLocation = place.value.location;

    // 更新 `routePairs`（確保新的 `origin` 和 `destination` 被記錄）
    placeStore.updateRoutePair(
      selectedDate,
      newIndex - 1,
      previousPlace,
      newPlaceLocation
    );
    console.log(
      `🚗 設定路徑: ${previousPlace.lat}, ${previousPlace.lng} ➡ ${newPlaceLocation.lat}, ${newPlaceLocation.lng}`
    );
  }

  // Swal.fire({
  //   title: "已加入行程",
  //   icon: "success",
  //   timer: 1000,
  //   showConfirmButton: false,
  // });
};
</script>

<style scoped>
.place-details {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px); /* 減去 navbar 高度 */
  overflow: hidden;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  position: relative; /* 讓 .button-container 絕對對齊底部 */
}

/* 讓內容區域可以滾動 */
.text-info {
  flex: 1; /* 讓內容區域佔滿剩餘空間 */
  padding: 20px;
  overflow-y: auto; /* 內容超出時可滾動 */
  min-height: 0; /* 避免內容過長撐開 */
}

/* 照片區塊 */
.photo-gallery-container {
  display: flex;
  justify-content: center;
  width: 100%;
  padding: 10px;
  max-height: 320px; /* 限制最大高度 */
  overflow: hidden; /* 防止多餘空白 */
}

.photo-gallery {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  scroll-behavior: smooth;
  max-width: 100%;
  padding: 10px;
}

.photo-gallery img {
  width: auto;
  height: 300px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease-in-out;
}

.photo-gallery img:hover {
  transform: scale(1.05);
}

/* 按鈕區域固定置底，不影響滾動 */
.button-container {
  position: sticky;
  bottom: 0;
  left: 0;
  width: 100%;
  background: white;
  padding: 15px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.15);
  border-radius: 0 0 12px 12px;
  z-index: 10;
}

/* 按鈕樣式 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.action-buttons button {
  padding: 12px 20px;
  font-size: 16px;
  font-weight: bold;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s ease, transform 0.2s ease;
}

.action-buttons button:hover {
  background: #0056b3;
  transform: scale(1.05);
}
</style>
