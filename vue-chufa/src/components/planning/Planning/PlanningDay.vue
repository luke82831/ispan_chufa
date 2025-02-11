<template>
  <div class="space-y-6">
    <h3 class="text-2xl font-semibold text-gray-900">
      {{ formattedSelectedDate }} 的行程
    </h3>

    <div class="departure-time">
      <label>出發時間：</label>
      <input
        type="time"
        v-model="departureTime"
        @change="updateDepartureTime"
      />
    </div>

    <!-- 顯示當天的行程 -->
    <div v-if="itineraryForSelectedDay.length" class="itinerary-list">
      <draggable
        v-model="itineraryForSelectedDay"
        :group="{ name: 'places', pull: 'clone', put: true }"
        :animation="250"
        item-key="placeOrder"
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
                    :date="formattedSelectedDate"
                    :departureTime="departureTime"
                    :itinerary="itineraryForSelectedDay"
                    :stayDurations="
                      itineraryStore.stayDurations?.[formattedSelectedDate] ??
                      {}
                    "
                    :index="index"
                  />

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
                    v-model="editingStayTimes[element.id]"
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
                    <h4 class="location-title">{{ element.placeName }}</h4>
                    <p class="location-address">{{ element.placeAddress }}</p>
                  </div>
                </div>
              </div>
            </li>

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
      <p>今天還沒有新增行程！</p>
    </div>
  </div>
</template>

<script setup>
import { onBeforeRouteLeave } from "vue-router";
import { onMounted, onUnmounted, ref, computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { useEventStore } from "@/stores/EventStore";
import { usePlaceStore } from "@/stores/PlaceStore";
import RouteTime from "./RouteTime.vue";
import draggable from "vuedraggable";
import StayTime from "./StayTime.vue";

const props = defineProps({
  selectedDate: String,
});

watch(
  () => props.selectedDate,
  (newDate) => {
    console.log("📅 `PlanningDay.vue` 收到的 `selectedDate`: ", newDate);
  },
  { immediate: true }
);

const itineraryStore = useItineraryStore();
const scheduleStore = useScheduleStore();
const eventStore = useEventStore();
const placeStore = usePlaceStore();

const hasUnsavedChanges = ref(false); // 追蹤是否有變更
const eventData = ref({}); // 儲存從後端載入的行程數據
const editingStayTimes = ref({}); // 存放每個地點的暫存停留時間

const updateDepartureTime = (event) => {
  const newTime = event.target.value;
  itineraryStore.setStartTime(formattedSelectedDate.value, newTime);
  hasUnsavedChanges.value = true; // 標記變更
};

const departureTime = computed({
  get: () => itineraryStore.getStartTime(formattedSelectedDate.value),
  set: (newTime) => {
    itineraryStore.setStartTime(formattedSelectedDate.value, newTime);
    hasUnsavedChanges.value = true; // 標記有變更
  },
});

// 確保 selectedDate 轉成 "YYYY-MM-DD" 格式
const formattedSelectedDate = computed(() => {
  if (!props.selectedDate) return "";
  const cleanedDate = props.selectedDate.replace(/[^0-9\/]/g, "");
  if (cleanedDate.includes("-")) return cleanedDate;

  const baseYear =
    scheduleStore.currentSchedule?.startDate?.split("-")[0] ||
    new Date().getFullYear();
  const [month, day] = cleanedDate
    .split("/")
    .map((num) => num.padStart(2, "0"));
  return `${baseYear}-${month}-${day}`;
});

// **讀取後端資料，並存入 Pinia**
watch(
  () => formattedSelectedDate.value,
  async (newDate) => {
    if (!newDate) return;

    console.log(`📅 選擇的日期: ${newDate}`);

    // 🚀 **從後端獲取行程資料**
    const event =
      (await eventStore.fetchEventByDate(
        scheduleStore.currentSchedule.tripId,
        newDate
      )) || {};

    console.log("🔍 從後端獲取的 `event`: ", event);

    eventData.value = { eventId: event.eventId ?? null };

    let placesWithDetails = [];
    if (event.eventXPlaceBeans) {
      console.log(
        "📍 從後端獲取的 `eventXPlaceBeans`:",
        event.eventXPlaceBeans
      );

      const placeIds = event.eventXPlaceBeans.map((e) => e.placeId);
      console.log("📍 需要加載的地點 ID:", placeIds);

      await placeStore.fetchMultiplePlaces(placeIds);
      console.log(
        "✅ `placeStore.placeDetailsMap`:",
        placeStore.placeDetailsMap
      );

      // ✅ 確保 `placeDetailsMap` 內有完整資料
      placesWithDetails = event.eventXPlaceBeans.map((eventPlace) => {
        const placeDetails = placeStore.getPlaceDetailById(eventPlace.placeId);

        return {
          ...eventPlace,
          placeName: placeDetails?.placeName ?? "未知地點",
          placeAddress: placeDetails?.placeAddress ?? "未知地址",
          photos: placeDetails?.photos ?? [], // ✅ 存入照片
          latitude: placeDetails?.latitude ?? null, // ✅ 存入緯度
          longitude: placeDetails?.longitude ?? null, // ✅ 存入經度
        };
      });
    }

    console.log("✅ 處理後的 `placesWithDetails`:", placesWithDetails);

    // ✅ 存入 Pinia
    itineraryStore.setItinerary(newDate, placesWithDetails);
    itineraryStore.setStartTime(newDate, event.startTime ?? "08:00");

    console.log(
      "✅ 已存入 Pinia：",
      itineraryStore.getItineraryForDay(newDate)
    );
  },
  { immediate: true }
);

// **從 Pinia 獲取當天的行程**
const itineraryForSelectedDay = computed({
  get: () => {
    const date = formattedSelectedDate.value;
    if (!date) return [];

    const itinerary = itineraryStore.getItineraryForDay(date);
    console.log(`📌 itineraryForSelectedDay (${date}):`, itinerary);

    return itinerary ?? [];
  },
  set: (newItinerary) => {
    const date = formattedSelectedDate.value;
    if (!date) return;
    console.log(`✏️ 更新行程 (${date}):`, newItinerary);

    itineraryStore.setItinerary(date, newItinerary);
  },
});

// **拖曳結束時更新 Pinia**
const handleDragEnd = () => {
  const date = formattedSelectedDate.value;
  if (!date) return;

  console.log("🔄 拖曳結束，重新排序 placeOrder");

  // ✅ 確保新的順序與 placeOrder 一致
  itineraryForSelectedDay.value.forEach((place, index) => {
    place.placeOrder = index + 1; // **讓 placeOrder 根據新順序重新編號**
  });

  console.log("📝 新的行程順序：", itineraryForSelectedDay.value);

  // ✅ 存回 Pinia
  itineraryStore.setItinerary(date, [...itineraryForSelectedDay.value]);
  hasUnsavedChanges.value = true; // **標記數據變更**
};

// **前端刪除景點**
const deletePlace = (index) => {
  itineraryStore.removePlace(formattedSelectedDate.value, index);
  hasUnsavedChanges.value = true;
};

// **編輯停留時間**
const editStayTime = (place) => {
  place.isEditingStay = true;

  // 確保編輯時，每個地點的 `stayDuration` 是獨立的
  editingStayTimes.value = { ...editingStayTimes.value };
  editingStayTimes.value[place.id] = itineraryStore.getStayDuration(
    formattedSelectedDate.value,
    place.id
  );
};

// **儲存新的停留時間**
const saveStayTime = (place) => {
  if (editingStayTimes.value[place.id] !== undefined) {
    const newDuration = Number(editingStayTimes.value[place.id]);

    itineraryStore.setStayDuration(
      formattedSelectedDate.value,
      place.id,
      newDuration
    );
  }

  place.isEditingStay = false;

  // 清除該地點的暫存值
  editingStayTimes.value = { ...editingStayTimes.value };
  delete editingStayTimes.value[place.id];
};

const getPhotoUrl = (photo) => {
  if (!photo) return ""; // 確保 photo 不為 null 或 undefined
  if (typeof photo === "object" && photo.hasOwnProperty("url")) {
    return photo.url; // 如果 photo 是一個物件，取 `url`
  }
  return photo; // 如果 photo 已經是 URL 字串，直接回傳
};

// **監聽行程變更，標記未儲存**
watch(
  itineraryForSelectedDay,
  () => {
    hasUnsavedChanges.value = true;
  },
  { deep: true }
);

// **離開畫面時，將變更儲存至後端**
onBeforeRouteLeave(async (to, from, next) => {
  if (!hasUnsavedChanges.value) {
    next();
    return;
  }

  if (!eventData.value?.eventId) {
    console.warn("⚠️ 沒有 eventId，不需要同步");
    next();
    return;
  }

  try {
    console.log("🚀 儲存行程變更到後端...");
    await eventStore.updateEvent(eventData.value.eventId, {
      places: itineraryForSelectedDay.value.map(({ placeId, placeOrder }) => ({
        placeId,
        placeOrder,
      })),
      startTime: itineraryStore.getStartTime(formattedSelectedDate.value), // 存入 Pinia 內的 startTime
    });
    console.log("✅ 儲存完成");
    hasUnsavedChanges.value = false;
    next();
  } catch (error) {
    console.error("❌ 儲存失敗", error);
    if (confirm("變更未儲存，是否仍要離開？")) {
      next();
    } else {
      next(false);
    }
  }
});

// **瀏覽器關閉時提醒**
const warnUnsavedChanges = (event) => {
  if (hasUnsavedChanges.value) {
    event.preventDefault();
    event.returnValue = "你有未儲存的變更，確定要離開嗎？";
  }
};

onMounted(() => {
  window.addEventListener("beforeunload", warnUnsavedChanges);
});

onUnmounted(() => {
  window.removeEventListener("beforeunload", warnUnsavedChanges);
});
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
