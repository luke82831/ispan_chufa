<template>
  <div class="space-y-6">
    <h3 class="text-2xl font-semibold text-gray-900">
      {{ selectedDate }} 的行程
    </h3>

    <!-- 設定出發時間 -->
    <div class="departure-time">
      <label>出發時間：</label>
      <input type="time" v-model="departureTime" />
    </div>

    <!-- 顯示當天的行程 -->
    <div v-if="itineraryForSelectedDay.length" class="itinerary-list">
      <draggable
        v-model="itineraryForSelectedDay"
        :group="{ name: 'places', pull: 'clone', put: true }"
        :animation="250"
        item-key="id"
        @end="handleDragEnd"
      >
        <template #item="{ element, index }">
          <ul class="itinerary-item-list">
            <li class="itinerary-item">
              <button @click="deletePlace(index)" class="delete-button">
                ✖
              </button>
              <div class="itinerary-details">
                <div class="stay-time-header">
                  <StayTime
                    :date="selectedDate"
                    :departureTime="departureTime"
                    :itinerary="itineraryForSelectedDay"
                    :stayDurations="
                      itineraryStore.stayDurations[selectedDate] || {}
                    "
                    :index="index"
                  />
                  <!-- 顯示超連結模式 -->
                  <a
                    v-if="!element.isEditingStay"
                    href="#"
                    @click.prevent="editStayTime(element)"
                    class="stay-duration-link"
                  >
                    {{
                      itineraryStore.getStayDuration(selectedDate, element.id)
                    }}
                    分鐘
                  </a>
                  <!-- 編輯模式 -->
                  <input
                    v-else
                    type="number"
                    v-model="element.tempStayDuration"
                    class="stay-duration-input"
                    @blur="saveStayTime(element)"
                    @keyup.enter="saveStayTime(element)"
                  />
                </div>
                <div class="itinerary-info">
                  <img
                    :src="getPhotoUrl(element.photos[0])"
                    v-if="element.photos && element.photos.length"
                    alt="Location Image"
                    class="location-image"
                  />
                  <div>
                    <h4 class="location-title">{{ element.displayName }}</h4>
                    <p class="location-address">
                      {{ element.formattedAddress }}
                    </p>
                  </div>
                </div>
              </div>
            </li>

            <!-- 顯示路徑時間 -->
            <div
              v-if="index < itineraryForSelectedDay.length - 1"
              class="route-time"
            >
              <route-time :date="selectedDate" :index="index" />
            </div>
          </ul>
        </template>
      </draggable>
    </div>

    <div v-else class="no-itinerary">
      <p>今天還沒有新增行程！</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { usePlaceStore } from "@/stores/PlaceStore";
import RouteTime from "./RouteTime.vue";
import draggable from "vuedraggable";
import StayTime from "./StayTime.vue";

// 取得傳入的日期參數
const props = defineProps({
  selectedDate: String,
});

const itineraryStore = useItineraryStore();
const placeStore = usePlaceStore();
const departureTime = ref("08:00"); // 預設早上 08:00

const itineraryForSelectedDay = computed({
  get: () => itineraryStore.getItineraryForDay(props.selectedDate),
  set: (newItinerary) => {
    itineraryStore.itineraryDates[props.selectedDate] = newItinerary;
  },
});

// **刪除地點並更新 routePairs**
const deletePlace = (index) => {
  console.log(`🗑 刪除行程: ${index}`);
  itineraryStore.removePlaceFromItinerary(props.selectedDate, index);
  updateRoutePairs();
};

// **拖曳地點後，更新 routePairs 和停留時間**
const handleDragEnd = () => {
  updateRoutePairs();
};

// **更新 placeStore.routePairs**
const updateRoutePairs = () => {
  placeStore.routePairs[props.selectedDate] = {}; // 清除舊資料

  for (let i = 0; i < itineraryForSelectedDay.value.length - 1; i++) {
    const origin = itineraryForSelectedDay.value[i].location;
    const destination = itineraryForSelectedDay.value[i + 1].location;

    placeStore.updateRoutePair(props.selectedDate, i, origin, destination);
  }
};

// **編輯停留時間**
const editStayTime = (place) => {
  place.isEditingStay = true;
  place.tempStayDuration = itineraryStore.getStayDuration(
    props.selectedDate,
    place.id
  );
};

// **儲存新的停留時間**
const saveStayTime = (place) => {
  const newDuration = Number(place.tempStayDuration);
  itineraryStore.setStayDuration(props.selectedDate, place.id, newDuration);
  place.isEditingStay = false;
};

// 獲取照片 URL 的方法
const getPhotoUrl = (photo) => {
  return photo; // 假設你有其他方法處理 URL
};

// **監聽 routePairs 的變化，確保時間重新計算**
watch(
  () => placeStore.routePairs[props.selectedDate],
  (newVal) => {
    if (newVal) {
      console.log("✅ 觸發計算，開始更新路徑時間");
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped>
.itinerary-item {
  background: #fff;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  position: relative;
  transition: all 0.2s ease-in-out;
  list-style: none;
}

.itinerary-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.itinerary-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.location-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
}

.stay-time-header {
  display: flex;
  justify-content: space-between;
  align-items: center; /* 確保內容垂直置中 */
  font-size: 0.875rem;
  font-weight: 600;
  margin-bottom: 4px;
  padding-right: 30px; /* 預留空間，避免與刪除按鈕重疊 */
}

.stay-duration-link {
  color: #3b82f6;
  text-decoration: underline;
  cursor: pointer;
}

.stay-duration-input {
  border: 1px solid #ccc;
  padding: 4px 8px;
  width: 50px;
  border-radius: 6px;
  font-size: 0.875rem;
}

.location-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
}

.location-address {
  font-size: 0.75rem;
  color: #6b7280;
}

.delete-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  color: #d32f2f;
  font-size: 1rem;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease-in-out;
}

.delete-button:hover {
  color: #b91c1c;
}
</style>
