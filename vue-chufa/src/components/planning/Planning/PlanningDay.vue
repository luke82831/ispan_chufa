<template>
  <div class="space-y-6">
    <!-- 使用 formattedSelectedDate (在 template 中可直接寫 {{ formattedSelectedDate }} ) -->
    <h3 class="text-2xl font-semibold text-gray-900">
      {{ formattedSelectedDate }} 的行程
    </h3>
    <p class="text-sm text-red-500">
      Debug: placeIds = {{ eventData?.placeIds }}
    </p>

    <!-- 設定出發時間：使用雙向綁定 departureTime -->
    <div class="departure-time">
      <label>出發時間：</label>
      <input type="time" v-model="departureTime" />
      <!-- 
        說明： 
        - 如果你還想在失焦（blur）時手動呼叫 updateStartTime，可再加上 @blur="updateStartTime"
        - 但因為我們在 Script 裡的 departureTime set() 已呼叫 updateStartTime，所以這裡可省略
      -->
    </div>

    <!-- 顯示該 event 內的所有地點 -->
    <div v-if="placesForEvent.length">
      <h3>當天的所有地點：</h3>
      <ul>
        <li v-for="place in placesForEvent" :key="place.placeId">
          {{ place.displayName }} - {{ place.formattedAddress }}
        </li>
      </ul>
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
                  <!-- 
                    傳入 formattedSelectedDate 取代原本 selectedDate
                    同時也傳入正確的 stayDurations key 
                  -->
                  <StayTime
                    :date="formattedSelectedDate"
                    :departureTime="departureTime"
                    :itinerary="itineraryForSelectedDay"
                    :stayDurations="
                      itineraryStore.stayDurations[formattedSelectedDate] || {}
                    "
                    :index="index"
                  />

                  <!-- 顯示/編輯停留時間 -->
                  <a
                    v-if="!element.isEditingStay"
                    href="#"
                    @click.prevent="editStayTime(element)"
                    class="stay-duration-link"
                  >
                    {{
                      itineraryStore.getStayDuration(
                        formattedSelectedDate,
                        element.id
                      )
                    }}
                    分鐘
                  </a>
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

            <!-- 顯示路徑時間：把 date 換成 formattedSelectedDate -->
            <div
              v-if="index < itineraryForSelectedDay.length - 1"
              class="route-time"
            >
              <RouteTime :date="formattedSelectedDate" :index="index" />
            </div>
          </ul>
        </template>
      </draggable>
    </div>

    <div v-else>
      <!-- 若沒有行程 -->
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

//本地只留一個 eventData，用來顯示/控制當天的 event 狀態
//後端/Store 回傳後，存 { eventId, date, placeIds, startTime, ... }
const eventData = ref({});
watch(
  () => eventData.value,
  (newEventData) => {
    console.log("🔍 eventData:", newEventData);
    console.log("📍 placeIds = ", newEventData?.placeIds || "沒有 placeIds");
  },
  { immediate: true }
);

//取得當天的 placeIds
const placeIdsForEvent = computed(() => {
  return eventData.value?.placeIds || [];
});
watch(
  placeIdsForEvent,
  async (newPlaceIds) => {
    console.log("🔍 監聽到 placeIdsForEvent 變更:", newPlaceIds);
    if (newPlaceIds.length === 0) {
      console.warn("⚠️ 沒有 placeIds，無法取得地點資料！");
      return;
    }

    console.log("📡 嘗試從 API 取得地點資料:", newPlaceIds);
    await placeStore.fetchMultiplePlaces(newPlaceIds);
    console.log("✅ 已載入的地點:", placeStore.placeDetailsMap);
  },
  { immediate: true }
);

//獲取 places 資料
const placesForEvent = computed(() => {
  return placeIdsForEvent.value
    .map((id) => placeStore.getPlaceDetailById(id)) // ✅ 從 store 取得詳細資訊
    .filter((place) => place); // 過濾掉 undefined
});

//確保 selectedDate 轉成 "YYYY-MM-DD" 格式
const formattedSelectedDate = computed(() => {
  if (!props.selectedDate) return "";

  // 移除非數字與斜線
  const cleanedDate = props.selectedDate.replace(/[^0-9\/]/g, "");

  // 已經是 YYYY-MM-DD 就直接回傳
  if (cleanedDate.includes("-")) return cleanedDate;

  // 若是 M/D 格式，轉成 YYYY-MM-DD
  const baseYear =
    scheduleStore.currentSchedule?.startDate?.split("-")[0] ||
    new Date().getFullYear();
  const [month, day] = cleanedDate
    .split("/")
    .map((num) => num.padStart(2, "0"));

  const formattedDate = `${baseYear}-${month}-${day}`;
  console.log(`📅 formattedSelectedDate 計算結果: ${formattedDate}`);
  return formattedDate;
});

/**
 * 監聽修正後的 selectedDate，去 EventStore 取（或抓）當天的 event
 */
watch(
  () => formattedSelectedDate.value,
  async (newDate) => {
    if (!newDate) return;

    console.log(`📅 修正後的 selectedDate: ${newDate}`);

    const event = await eventStore.fetchEventByDate(
      scheduleStore.currentSchedule.tripId,
      newDate
    );

    if (event) {
      eventData.value = { ...event };
      console.log(`🚀 從後端載入 startTime: ${eventData.value.startTime}`);
    } else {
      console.warn(`⚠️ ${newDate} 沒有行程內容`);
      eventData.value = {};
    }
  },
  { immediate: true }
);

/**
 * 確保選中的日期同步到 scheduleStore (全域)
 */
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

/**
 * 更新 startTime 到後端
 */
const updateStartTime = async (newTime) => {
  if (!eventData.value || !eventData.value.eventId) return;

  console.log(`🔄 更新後端 startTime 為: ${newTime}`);
  await eventStore.updateEvent(eventData.value.eventId, {
    startTime: newTime,
  });
  console.log(`✅ 更新完成`);
};

/**
 * 計存「每個日期的預設出發時間」，若和後端無法即時同步，可作本地暫存
 */
const departureTimes = ref({});

/**
 * 雙向綁定 startTime (computed)
 * get -> 取 eventData.value.startTime
 * set -> 呼叫 updateStartTime
 */
const departureTime = computed({
  get: () => {
    return eventData.value?.startTime || "08:00";
  },
  set: async (newTime) => {
    if (!eventData.value || !eventData.value.eventId) return;
    eventData.value.startTime = newTime;
    await updateStartTime(newTime);
  },
});

/**
 * 取得/設定「當天的行程陣列」，來自 itineraryStore
 * 假設 itineraryStore 使用 Option API => 可直接存取 .itineraryDates[date]
 */
const itineraryForSelectedDay = computed({
  get: () => {
    return itineraryStore.getItineraryForDay(formattedSelectedDate.value);
  },
  set: (newItinerary) => {
    // 如果 itineraryDates 是 Option API state，可直接：
    itineraryStore.itineraryDates[formattedSelectedDate.value] = newItinerary;

    // 若 itineraryDates 是 ref({})，則需要：
    // itineraryStore.itineraryDates.value[formattedSelectedDate.value] = newItinerary;
  },
});

/**
 * 刪除地點並更新 routePairs
 */
const deletePlace = (index) => {
  console.log(`🗑 刪除行程: ${index}`);
  // 注意：formattedSelectedDate 是 computed -> 取值要加 .value
  itineraryStore.removePlaceFromItinerary(formattedSelectedDate.value, index);
  updateRoutePairs();
};

/**
 * 拖曳地點後，更新 routePairs 和停留時間
 */
const handleDragEnd = () => {
  updateRoutePairs();
};

/**
 * 更新 placeStore.routePairs
 * 假設 placeStore.routePairs 是 Option API (state = { routePairs: {} })
 */
const updateRoutePairs = () => {
  const date = formattedSelectedDate.value;
  // 清除舊資料
  placeStore.routePairs[date] = {};

  // itineraryForSelectedDay 本身是 computed，需要 .value 取得陣列
  for (let i = 0; i < itineraryForSelectedDay.value.length - 1; i++) {
    const origin = itineraryForSelectedDay.value[i].location;
    const destination = itineraryForSelectedDay.value[i + 1].location;

    // 呼叫 store 裡的 action
    placeStore.updateRoutePair(date, i, origin, destination);
  }
};

/**
 * 編輯停留時間
 */
const editStayTime = (place) => {
  place.isEditingStay = true;
  place.tempStayDuration = itineraryStore.getStayDuration(
    formattedSelectedDate.value,
    place.id
  );
};

/**
 * 儲存新的停留時間
 */
const saveStayTime = (place) => {
  const newDuration = Number(place.tempStayDuration);
  itineraryStore.setStayDuration(
    formattedSelectedDate.value,
    place.id,
    newDuration
  );
  place.isEditingStay = false;
};

/**
 * 獲取照片 URL
 */
const getPhotoUrl = (photo) => {
  return photo;
};

/**
 * 監聽 placeStore.routePairs[某日]，做相應處理
 * 如果 placeStore 是 Option API => placeStore.routePairs[date]
 * 若是 ref => placeStore.routePairs.value[date]
 */
//暫時註解
// watch(
//   () => placeStore.routePairs[formattedSelectedDate.value],
//   (newVal) => {
//     if (newVal) {
//       console.log("✅ 觸發計算，開始更新路徑時間:", newVal);
//       // 這裡可以呼叫計算邏輯或後端 API
//     }
//   },
//   { immediate: true, deep: true }
// );

/**
 * 確保初始出發時間
 */
watch(
  () => formattedSelectedDate.value,
  (newDate) => {
    if (!newDate) return;
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
