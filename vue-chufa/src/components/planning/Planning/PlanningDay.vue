<template>
  <div class="space-y-6">
    <h3 class="text-2xl font-semibold text-gray-900">{{ selectedDate }} 的行程</h3>

    <!-- 設定出發時間 -->
    <div class="departure-time">
      <label>出發時間：</label>
      <input type="time" v-model="eventData.startTime" @blur="updateStartTime" />
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
              <button @click="deletePlace(index)" class="delete-button">✖</button>
              <div class="itinerary-details">
                <div class="stay-time-header">
                  <StayTime
                    :date="selectedDate"
                    :departureTime="departureTime"
                    :itinerary="itineraryForSelectedDay"
                    :stayDurations="itineraryStore.stayDurations[selectedDate] || {}"
                    :index="index"
                  />
                  <!-- 顯示超連結模式 -->
                  <a
                    v-if="!element.isEditingStay"
                    href="#"
                    @click.prevent="editStayTime(element)"
                    class="stay-duration-link"
                  >
                    {{ itineraryStore.getStayDuration(selectedDate, element.id) }}
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
            <div v-if="index < itineraryForSelectedDay.length - 1" class="route-time">
              <route-time :date="selectedDate" :index="index" />
            </div>
          </ul>
        </template>
      </draggable>
    </div>

    <div v-if="!eventData">
      <p>今天還沒有新增行程！</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { useEventStore } from "@/stores/EventStore";
import { usePlaceStore } from "@/stores/PlaceStore";
import RouteTime from "./RouteTime.vue";
import draggable from "vuedraggable";
import StayTime from "./StayTime.vue";

// 取得傳入的日期參數
const props = defineProps({
  selectedDate: String,
});

const itineraryStore = useItineraryStore();
const scheduleStore = useScheduleStore();
const eventStore = useEventStore();
const placeStore = usePlaceStore();
const eventData = ref({});

// 取得修正後的 selectedDate
const formattedSelectedDate = computed(() => {
  if (!props.selectedDate) return "";

  // **確保 selectedDate 只有數字與 `/`，避免意外字串**
  const cleanedDate = props.selectedDate.replace(/[^0-9\/]/g, "");

  // **如果已經是 `YYYY-MM-DD`，直接回傳**
  if (cleanedDate.includes("-")) return cleanedDate;

  // **從 schedule.startDate 取得年份**
  const baseYear =
    scheduleStore.currentSchedule?.startDate?.split("-")[0] || new Date().getFullYear();

  // **確保 `M/D` 變成 `MM-DD`（補 0）**
  const [month, day] = cleanedDate.split("/").map((num) => num.padStart(2, "0"));

  // **回傳 `YYYY-MM-DD` 格式**
  return `${baseYear}-${month}-${day}`;
});

// **監聽 selectedDate，確保載入當天的 event**
watch(
  () => formattedSelectedDate.value,
  async (newDate) => {
    if (!newDate) return;

    console.log(`📅 修正後的 selectedDate: ${newDate}`);
    let event = await eventStore.fetchEventByDate(
      scheduleStore.currentSchedule.tripId,
      newDate
    );

    if (event) {
      eventData.value = { ...event }; // ✅ 確保 eventData.value 存入 API 回傳的值
      console.log(`🚀 從後端載入 startTime: ${eventData.value.startTime}`);
    } else {
      console.warn(`⚠️ ${newDate} 沒有行程內容`);
      eventData.value = {};
    }
  },
  { immediate: true }
);

// **確保選中的日期同步到 scheduleStore**
watch(
  () => formattedSelectedDate.value,
  (newDate) => {
    if (newDate) {
      console.log(`📅 選擇的日期更新: ${newDate}`);
      scheduleStore.setSelectedDate(newDate);
    }
  },
  { immediate: true }
);

// **更新 Start Time**
const updateStartTime = async (newTime) => {
  if (!eventData.value) return;

  console.log(`🔄 更新後端 startTime 為: ${newTime}`);
  await eventStore.updateEvent(eventData.value.event_id, {
    startTime: newTime,
  });

  console.log(`✅ 更新完成`);
};

// 存儲每個日期的出發時間
const departureTimes = ref({});
const departureTime = computed({
  get: () => {
    return eventData.value?.startTime || "08:00"; // ✅ 預設值 08:00
  },
  set: async (newTime) => {
    if (!eventData.value) return;
    eventData.value.startTime = newTime; // 更新本地資料
    await updateStartTime(newTime); // ✅ 更新後端
  },
});

const itineraryForSelectedDay = computed({
  get: () => itineraryStore.getItineraryForDay(formattedSelectedDate.value),
  set: (newItinerary) => {
    itineraryStore.itineraryDates[formattedSelectedDate.value] = newItinerary;
  },
});

// **刪除地點並更新 routePairs**
const deletePlace = (index) => {
  console.log(`🗑 刪除行程: ${index}`);
  itineraryStore.removePlaceFromItinerary(formattedSelectedDate.value, index);
  updateRoutePairs();
};

// **拖曳地點後，更新 routePairs 和停留時間**
const handleDragEnd = () => {
  updateRoutePairs();
};

// **更新 placeStore.routePairs**
const updateRoutePairs = () => {
  placeStore.routePairs[formattedSelectedDate.value] = {}; // 清除舊資料

  for (let i = 0; i < itineraryForSelectedDay.value.length - 1; i++) {
    const origin = itineraryForSelectedDay.value[i].location;
    const destination = itineraryForSelectedDay.value[i + 1].location;

    placeStore.updateRoutePair(formattedSelectedDate.value, i, origin, destination);
  }
};

// **編輯停留時間**
const editStayTime = (place) => {
  place.isEditingStay = true;
  place.tempStayDuration = itineraryStore.getStayDuration(
    formattedSelectedDate.value,
    place.id
  );
};

// **儲存新的停留時間**
const saveStayTime = (place) => {
  const newDuration = Number(place.tempStayDuration);
  itineraryStore.setStayDuration(formattedSelectedDate.value, place.id, newDuration);
  place.isEditingStay = false;
};

// 獲取照片 URL 的方法
const getPhotoUrl = (photo) => {
  return photo; // 假設你有其他方法處理 URL
};

// **監聽 routePairs，確保時間重新計算**
watch(
  () => placeStore.routePairs[formattedSelectedDate.value],
  (newVal) => {
    if (newVal) {
      console.log("✅ 觸發計算，開始更新路徑時間");
    }
  },
  { immediate: true, deep: true }
);

// **確保初始出發時間**
watch(
  () => formattedSelectedDate.value,
  (newDate) => {
    if (!(newDate in departureTimes.value)) {
      departureTimes.value[newDate] = "08:00";
    }
  },
  { immediate: true }
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
