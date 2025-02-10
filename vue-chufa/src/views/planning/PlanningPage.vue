<template>
  <div class="container">
    <!-- event -->
    <div class="form-container">
      <ItineraryTabs></ItineraryTabs>
    </div>

    <!-- placedetails (only show if a place is selected) -->
    <!-- `@place-selected="handlePlaceChanged"` 監聽事件 -->
    <div v-if="selectedPlaceDetail" class="place-container">
      <PlaceDetail :place="selectedPlaceDetail" />

      <!-- 按鈕區域 -->
      <div class="button-container">
        <div class="action-buttons">
          <button @click="savePlace">儲存地點</button>
          <button @click="addPlaceToEvent">加入行程</button>
        </div>
      </div>
    </div>

    <!-- map -->
    <div class="map-container">
      <MapDisplay />
    </div>
  </div>
</template>

<script setup>
import { watch, computed } from "vue";
import { usePlaceStore } from "@/stores/PlaceStore";
import { useEventStore } from "@/stores/EventStore";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { useEventPlaceStore } from "@/stores/EventPlaceStore"; // 新增
import Swal from "sweetalert2";

import MapDisplay from "@/components/planning/GoogleMap/MapDisplay.vue";
import PlaceDetail from "@/components/planning/GoogleMap/PlaceDetail.vue";
import ItineraryTabs from "@/components/planning/Planning/ItineraryTabs.vue";

const scheduleStore = useScheduleStore();
const eventStore = useEventStore();
const placeStore = usePlaceStore();
const eventPlaceStore = useEventPlaceStore();

// ✅ `selectedPlaceDetail` 來自 `Pinia Store`
const selectedPlaceDetail = computed(() => placeStore.selectedPlaceDetail);

// ✅ `handlePlaceChanged()` 更新 `selectedPlaceId`
const handlePlaceChanged = (place) => {
  if (!place || !place.placeId) {
    console.warn("⚠️ 無效的地點資料", place);
    return;
  }

  console.log("📍 地點變更:", place);
  placeStore.selectedPlaceId = place.placeId; // 設定為選取的地點
  placeStore.savePlaceToMap(place); // 存入快取
};

// ✅ 監聽 `selectedPlaceDetail`，當地點變更時自動觸發 `handlePlaceChanged()`
watch(selectedPlaceDetail, (newPlace) => {
  if (newPlace && newPlace.placeId !== placeStore.placeId) {
    console.log("🔄 監聽到地點變更，觸發 handlePlaceChanged:", newPlace);
    handlePlaceChanged(newPlace);
  }
});

// ✅ `selectedDate` 來自 `scheduleStore`
const selectedDate = computed(() => scheduleStore.selectedDate);

// ✅ `selectedPlaceId` 來自 `placeStore`
const selectedPlaceId = computed(() => placeStore.selectedPlaceId);

// 加入行程（多對多）
const addPlaceToEvent = async () => {
  if (!selectedDate.value) {
    Swal.fire("請先選擇行程日期");
    return;
  }
  if (!selectedPlaceId.value) {
    Swal.fire("請先選擇地點");
    return;
  }

  // 透過 EventStore 的 getter 拿到這一天的 eventId
  const eventId = eventStore.getEventIdByDate(selectedDate.value);
  if (!eventId) {
    Swal.fire("該日期尚未建立行程 (Event)");
    return;
  }

  try {
    console.log(
      `📡 [加入行程] eventId: ${eventId}, placeId: ${selectedPlaceId.value}`
    );

    // 🔹 確保 API 請求成功
    const response = await eventPlaceStore.addPlaceToEvent(
      eventId,
      selectedPlaceId.value
    );

    // 🔹 確保回應資料有效
    if (!response || !response.eventmappingId) {
      console.log("response: " + response);
      console.log("response.eventmappingId: " + response.eventmappingId);
      throw new Error("API 回應錯誤，未返回有效數據");
    }

    // ✅ 重新拉取 `eventPlaceList`，確保狀態同步
    await eventPlaceStore.fetchPlacesByEvent(eventId);

    // ✅ 顯示成功訊息
    Swal.fire({
      title: "已加入行程",
      text: `成功將地點 ${selectedPlaceId.value} 加入 ${selectedDate.value} 的行程！`,
      icon: "success",
      timer: 1500,
      showConfirmButton: false,
    });
  } catch (error) {
    console.error("❌ [加入行程失敗]:", error);

    // ❌ 顯示 API 錯誤訊息（如果有）
    Swal.fire({
      title: "加入行程失敗",
      text: error.response?.data?.message || "伺服器錯誤，請稍後再試。",
      icon: "error",
    });
  }
};

// 儲存地點
const savePlace = () => {
  if (!placeDetails.value) {
    Swal.fire("地點資料未正確加載");
    return;
  }
  console.log("儲存地點:", placeDetails.value);
  Swal.fire({
    title: "已儲存景點",
    icon: "success",
    timer: 1500,
    showConfirmButton: false,
  });
};
</script>

<style>
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.container {
  display: flex;
  height: calc(100vh - 80px); /* 填滿視窗高度，扣除導覽列 60px */
  width: 100%; /* 填滿視窗寬度 */
  margin: 0;
  padding: 0;
}

.form-container {
  position: relative; /* 確保 z-index 運作 */
  flex: 0 0 25%; /* 固定 25% 寬度 */
  overflow-y: auto; /* 垂直滾動 */
  padding: 5px;
  box-shadow: 8px 0 16px rgba(0, 0, 0, 0.15); /* 陰影 */
  box-sizing: border-box; /* 確保 padding 不影響寬度計算 */
  z-index: 10; /* 讓表單顯示在地圖上方 */
  background-color: #f9f9f9;
  overflow-x: hidden; /* 隱藏水平滾動條 */
  word-wrap: break-word; /* 自動換行，避免溢出 */
  word-break: break-word; /* 將長單詞或字串強制換行 */
  white-space: normal; /* 確保內容不強制在一行內顯示 */
}

.map-container {
  flex: 1; /* 自動填滿剩餘空間 */
  position: relative; /* 確保內部絕對定位基於 .map-container */
}

.place-container {
  position: relative;
  top: 0;
  width: 25%; /* 占地圖區域的寬度 */
  height: 100%; /* 確保與 .map-container 一樣高 */
  z-index: 15; /* 保證 PlaceDetail 覆蓋在地圖之上 */
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2); /* 輕微陰影 */
  overflow-y: auto; /* 當內容超出高度時，允許垂直滾動 */
  overflow-x: hidden; /* 隱藏水平滾動條 */
  box-sizing: border-box; /* 包括 padding 在內計算寬高 */
  word-wrap: break-word; /* 自動換行，避免溢出 */
  word-break: break-word; /* 將長單詞或字串強制換行 */
  white-space: normal; /* 確保內容不強制在一行內顯示 */
}

.place-container::-webkit-scrollbar {
  display: none; /* 隱藏滾動條 */
}

/* 固定按鈕區塊在畫面底部 */
.button-container {
  position: sticky;
  bottom: 0;
  left: 0;
  width: 25vw; /* 讓寬度與 .place-container 相同 */
  background: white;
  padding: 15px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.15);
  z-index: 100;
  text-align: center;
}

/* 讓按鈕水平置中 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

/* 按鈕樣式 */
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
