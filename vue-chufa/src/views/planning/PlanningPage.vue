<template>
  <div class="container" @click="closePlaceDetail">
    <!-- event -->
    <div class="form-container">
      <ItineraryTabs></ItineraryTabs>
    </div>

    <!-- placedetails (only show if a place is selected) -->
    <div v-if="selectedPlaceDetail" class="place-container" @click.stop>
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
import { watch, computed, ref } from "vue";
import { usePlaceStore } from "@/stores/placeStore";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { useItineraryStore } from "@/stores/ItineraryStore"; // ✅ 新增 ItineraryStore
import Swal from "sweetalert2";

import MapDisplay from "@/components/planning/GoogleMap/MapDisplay.vue";
import PlaceDetail from "@/components/planning/GoogleMap/PlaceDetail.vue";
import ItineraryTabs from "@/components/planning/Planning/ItineraryTabs.vue";

const scheduleStore = useScheduleStore();
const placeStore = usePlaceStore();
const itineraryStore = useItineraryStore(); // ✅ 確保有導入 itineraryStore

const hasUnsavedChanges = ref(false);

const selectedPlaceDetail = computed(() => placeStore.selectedPlaceDetail);

const selectedDate = computed(() => scheduleStore.selectedDate || ""); // ✅ 確保不為 null

const selectedPlaceId = computed(() => placeStore.selectedPlaceId);

// 點擊其他區域時關閉 PlaceDetail
const closePlaceDetail = () => {
  placeStore.selectedPlaceId = null;
};

// ✅ 監聽 `selectedPlaceDetail`，當地點變更時自動觸發
watch(selectedPlaceDetail, (newPlace) => {
  if (newPlace && newPlace.placeId !== placeStore.placeId) {
    console.log("🔄 監聽到地點變更，觸發 handlePlaceChanged:", newPlace);
    placeStore.selectedPlaceId = newPlace.placeId; // ✅ 設定為選取的地點
    placeStore.savePlaceToMap(newPlace); // ✅ 存入快取
  }
});

// ✅ 確保 `selectedDate` 轉換為 "YYYY-MM-DD"
const formattedSelectedDate = computed(() => {
  if (!selectedDate.value) return "";

  const cleanedDate = selectedDate.value.replace(/[^0-9\/]/g, ""); // 移除不必要符號
  if (cleanedDate.includes("-")) return cleanedDate; // 如果已經是 YYYY-MM-DD 格式則直接回傳

  const baseYear =
    scheduleStore.currentSchedule?.startDate?.split("-")[0] || new Date().getFullYear();
  const [month, day] = cleanedDate.split("/").map((num) => num.padStart(2, "0"));

  return `${baseYear}-${month}-${day}`; // 轉換為 YYYY-MM-DD
});

const addPlaceToEvent = async () => {
  console.log("📅 選擇的行程日期: ", formattedSelectedDate.value); // ✅ 確保日期正確

  if (!formattedSelectedDate.value) {
    Swal.fire("請先選擇行程日期");
    return;
  }
  if (!selectedPlaceId.value) {
    Swal.fire("請先選擇地點");
    return;
  }

  try {
    console.log(
      `📡 [加入行程] date: ${formattedSelectedDate.value}, placeId: ${selectedPlaceId.value}`
    );

    // 🔹 取得該地點的完整資料
    const placeDetails = placeStore.getPlaceDetailById(selectedPlaceId.value);
    if (!placeDetails) {
      throw new Error("找不到該地點的詳細資訊");
    }

    // **取得當前日期的行程**
    const existingItinerary =
      itineraryStore.getItineraryForDay(formattedSelectedDate.value) ?? [];

    // **新增的地點物件**
    const newPlace = {
      placeId: selectedPlaceId.value,
      placeName: placeDetails.placeName ?? "未知地點",
      placeAddress: placeDetails.placeAddress ?? "未知地址",
      photos: [...(placeDetails.photos ?? [])], // ✅ 確保 `photos` 為標準 Array
      latitude: placeDetails.latitude ?? null,
      longitude: placeDetails.longitude ?? null,
      placeOrder: existingItinerary.length + 1, // **確保 placeOrder 正確**
      stayDuration: null, // 預設停留時間
      travelTime: null, // 預設行車時間
    };

    console.log("✅ [加入行程] 存入的地點資訊:", newPlace);

    // **更新 Pinia Store**
    itineraryStore.setItinerary(formattedSelectedDate.value, [
      ...existingItinerary,
      newPlace,
    ]);

    // **標記有變更，確保離開時同步到後端**
    hasUnsavedChanges.value = true;

    // **顯示成功訊息**
    Swal.fire({
      title: "已加入行程",
      text: `成功將地點「${newPlace.placeName}」加入 ${formattedSelectedDate.value} 的行程！`,
      icon: "success",
      timer: 1500,
      showConfirmButton: false,
    });
  } catch (error) {
    console.error("❌ [加入行程失敗]:", error);

    // ❌ 顯示錯誤訊息
    Swal.fire({
      title: "加入行程失敗",
      text: error.message || "系統錯誤，請稍後再試。",
      icon: "error",
    });
  }
};

// ✅ 儲存地點（測試用）
const savePlace = () => {
  const placeDetails = selectedPlaceDetail.value;
  if (!placeDetails) {
    Swal.fire("地點資料未正確加載");
    return;
  }
  console.log("💾 儲存地點:", placeDetails);
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
  flex: 0 0 30%; /* 固定 25% 寬度 */
  overflow-y: auto; /* 垂直滾動 */
  padding: 5px;
  box-shadow: 8px 0 16px rgba(0, 0, 0, 0.15); /* 陰影 */
  box-sizing: border-box; /* 確保 padding 不影響寬度計算 */
  z-index: 10; /* 讓表單顯示在地圖上方 */
  background-color: white;
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
  background-color: white;
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
  background: #2973b2;
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
