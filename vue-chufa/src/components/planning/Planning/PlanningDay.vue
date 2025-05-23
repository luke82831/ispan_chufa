<template>
  <div class="space-y-6">
    <h3 class="text-2xl font-semibold text-gray-900">
      {{ formattedSelectedDate }} 的行程
    </h3>

    <div class="departure-time">
      <label>出發時間：</label>
      <input type="time" v-model="departureTime" @change="updateDepartureTime" />
    </div>

    <div v-if="itineraryForSelectedDay.length" class="itinerary-list">
      <draggable
        v-model="itineraryForSelectedDay"
        :group="{ name: 'places', pull: 'clone', put: true }"
        :animation="250"
        item-key="index"
        @end="handleDragEnd"
      >
        <template #item="{ element, index }" :key="index">
          <ul class="itinerary-item-list">
            <li class="itinerary-item">
              <!-- 刪除按鈕 -->
              <button @click="deletePlace(index)" class="delete-button">✖</button>

              <div class="itinerary-details">
                <div class="stay-time-header">
                  <!-- 這裡呼叫 StayTime 組件 -->
                  <StayTime
                    :date="formattedSelectedDate"
                    :departureTime="departureTime"
                    :itinerary="itineraryForSelectedDay"
                    :index="index"
                  />

                  <!-- 顯示超連結模式 -->
                  <a
                    v-if="!isEditing(index)"
                    href="#"
                    @click.prevent="editStayTime(index)"
                    class="stay-duration-link"
                  >
                    {{ itineraryStore.getStayDuration(formattedSelectedDate, index) }}
                    分鐘
                  </a>

                  <!-- 編輯模式 -->
                  <input
                    v-else
                    type="number"
                    :value="tempValue(index)"
                    class="stay-duration-input"
                    @input="updateTempStayTime(index, $event)"
                    @blur="saveStayTime(index)"
                    @keyup.enter="saveStayTime(index)"
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

            <!-- 在行程之間插入 RouteTime -->
            <div v-if="index < itineraryForSelectedDay.length - 1" class="route-time">
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
import Swal from "sweetalert2";
import { onBeforeRouteLeave } from "vue-router";
import { onMounted, onUnmounted, ref, computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { useEventStore } from "@/stores/EventStore";
import { usePlaceStore } from "@/stores/PlaceStore";
import { useEventPlaceStore } from "@/stores/EventPlaceStore";

import RouteTime from "./RouteTime.vue";
import draggable from "vuedraggable";
import StayTime from "./StayTime.vue";

const props = defineProps({
  selectedDate: String,
});

// ------------- Pinia & Store -------------
const itineraryStore = useItineraryStore();
const scheduleStore = useScheduleStore();
const eventStore = useEventStore();
const placeStore = usePlaceStore();
const eventPlaceStore = useEventPlaceStore();

// ------------- UI 狀態 -------------
const hasUnsavedChanges = ref(false);
const eventData = ref({});

// ------------- 日期/時間轉換 -------------
const formattedSelectedDate = computed(() => {
  if (!props.selectedDate) return "";
  const cleanedDate = props.selectedDate.replace(/[^0-9\/]/g, "");
  if (cleanedDate.includes("-")) return cleanedDate;

  const baseYear =
    scheduleStore.currentSchedule?.startDate?.split("-")[0] || new Date().getFullYear();
  const [month, day] = cleanedDate.split("/").map((num) => num.padStart(2, "0"));
  return `${baseYear}-${month}-${day}`;
});

watch(
  () => props.selectedDate,
  (newDate) => {
    // console.log("📅 `PlanningDay.vue` 收到的 `selectedDate`: ", newDate);
  },
  { immediate: true }
);

const convertTimeToMinutes = (timeString) => {
  if (!timeString) return 0; // 預設為 0 分鐘

  if (typeof timeString === "string" && timeString.match(/^\d{2}:\d{2}:\d{2}$/)) {
    const [hours, minutes] = timeString.split(":").map(Number);
    return hours * 60 + minutes;
  }

  console.warn(`⚠️ 無法解析時間: ${timeString}`);
  return 0;
};

// ------------- 出發時間 -------------
const updateDepartureTime = (event) => {
  const newTime = event.target.value;
  itineraryStore.setStartTime(formattedSelectedDate.value, newTime);
  hasUnsavedChanges.value = true;
};

const departureTime = computed({
  get: () => itineraryStore.getStartTime(formattedSelectedDate.value),
  set: (newTime) => {
    itineraryStore.setStartTime(formattedSelectedDate.value, newTime);
    hasUnsavedChanges.value = true;
  },
});

// ------------- 從後端讀取資料 -------------
watch(
  () => formattedSelectedDate.value,
  async (newDate) => {
    if (!newDate) return;
    console.log(`📅 選擇的日期: ${newDate}`);

    // 從後端獲取行程資料
    const event =
      (await eventStore.fetchEventByDate(
        scheduleStore.currentSchedule.tripId,
        newDate
      )) || {};
    console.log("🔍 從後端獲取的 `event`: ", event);

    eventData.value = { eventId: event.eventId ?? null };

    let placesWithDetails = [];
    if (event.eventXPlaceBeans) {
      console.log("📍 從後端獲取的 `eventXPlaceBeans`:", event.eventXPlaceBeans);

      const placeIds = event.eventXPlaceBeans.map((e) => e.placeId);
      // console.log("📍 需要加載的地點 ID:", placeIds);

      await placeStore.fetchMultiplePlaces(placeIds);
      console.log("✅ `placeStore.placeDetailsMap`:", placeStore.placeDetailsMap);

      // **🔹 按照 placeOrder 排序**
      const sortedPlaces = event.eventXPlaceBeans.sort(
        (a, b) => a.placeOrder - b.placeOrder
      );
      console.log("📌 已排序的行程資料:", sortedPlaces);

      // **🚀 存入 `stayDuration` 到 `Pinia`**
      sortedPlaces.forEach((eventPlace, index) => {
        const stayDurationMinutes = eventPlace.stayDuration
          ? convertTimeToMinutes(eventPlace.stayDuration)
          : 0;

        itineraryStore.setStayDuration(newDate, index, stayDurationMinutes);
        console.log(
          `🕒 已存入 Pinia [${newDate}] index ${index} 的停留時間: ${stayDurationMinutes} 分鐘`
        );
      });

      // 將地點詳細資訊合併
      placesWithDetails = event.eventXPlaceBeans.map((eventPlace, index) => {
        const placeDetails = placeStore.getPlaceDetailById(eventPlace.placeId);
        console.log(`📍 eventPlace ${index}:`, eventPlace);

        return {
          ...eventPlace,
          index,
          placeName: placeDetails?.placeName ?? "未知地點",
          placeAddress: placeDetails?.placeAddress ?? "未知地址",
          photos: placeDetails?.photos ?? [],
          latitude: placeDetails?.latitude ?? null,
          longitude: placeDetails?.longitude ?? null,
        };
      });
    }

    console.log("✅ 排序後的 `placesWithDetails`:", placesWithDetails);

    // 存入 Pinia
    itineraryStore.setItinerary(newDate, placesWithDetails);
    itineraryStore.setStartTime(newDate, event.startTime ?? "08:00");
    console.log("✅ 已存入 Pinia：", itineraryStore.getItineraryForDay(newDate));
  },
  { immediate: true }
);

// ------------- 行程資料 (computed) -------------
const itineraryForSelectedDay = computed({
  get: () => {
    const date = formattedSelectedDate.value;
    if (!date) return [];
    return itineraryStore.getItineraryForDay(date) || [];
  },
  set: (newItinerary) => {
    const date = formattedSelectedDate.value;
    if (!date) return;
    itineraryStore.setItinerary(date, newItinerary);
  },
});

// ------------- 拖曳排序 -------------
const handleDragEnd = () => {
  const date = formattedSelectedDate.value;
  if (!date) return;

  // console.log("🔄 拖曳結束，更新行程順序");
  itineraryStore.setItinerary(date, [...itineraryForSelectedDay.value]);
  hasUnsavedChanges.value = true;
  console.log("🚨 拖曳變更已標記為未儲存！");
};

// ------------- 刪除地點 -------------
const deletePlace = (index) => {
  itineraryStore.removePlace(formattedSelectedDate.value, index);
  hasUnsavedChanges.value = true;
};

// ------------- 編輯模式相關 -------------
function isEditing(index) {
  // 從 Pinia 讀取 isEditingStays
  return itineraryStore.getIsEditingStay(formattedSelectedDate.value, index);
}

function tempValue(index) {
  // 讀取暫存的停留時間，確保即時更新
  return itineraryStore.getTempStayDuration(formattedSelectedDate.value, index);
}

const editStayTime = (index) => {
  const date = formattedSelectedDate.value;
  if (!date) return;

  // 1. 設定「正在編輯」
  itineraryStore.setIsEditingStay(date, index, true);

  // 2. 讀取「正式」的 `stayDuration` 作為初始值
  const currentDuration = itineraryStore.getStayDuration(date, index) || 0;
  itineraryStore.setTempStayDuration(date, index, currentDuration);
};

const updateTempStayTime = (index, event) => {
  const date = formattedSelectedDate.value;
  if (!date) return;

  // 讀取使用者輸入的數值
  const newDuration = isNaN(event.target.value) ? 0 : Number(event.target.value);

  // 即時更新 tempStayDurations，確保畫面同步變更
  itineraryStore.setTempStayDuration(date, index, newDuration);
};

const saveStayTime = (index) => {
  const date = formattedSelectedDate.value;
  if (!date) return;

  // 取得暫存的數值 (使用者輸入的數值)
  const finalDuration = itineraryStore.getTempStayDuration(date, index) || 0;

  // 1. 更新正式的停留時間
  itineraryStore.setStayDuration(date, index, finalDuration);

  // 2. 關閉編輯模式
  itineraryStore.setIsEditingStay(date, index, false);

  hasUnsavedChanges.value = true;
};

// ------------- 圖片處理 -------------
const getPhotoUrl = (photo) => {
  if (!photo) return "";
  if (typeof photo === "object" && photo.hasOwnProperty("url")) {
    return photo.url;
  }
  return photo;
};

// ------------- 監聽行程變更 -------------
watch(
  () => formattedSelectedDate.value, // 🔥 監聽選擇的日期，而不是 `itineraryForSelectedDay`
  async (newDate, oldDate) => {
    const eventPlaceStore = useEventPlaceStore();
    if (!oldDate || !eventData.value?.eventId) return; // 🔥 確保有舊日期，且 `eventId` 存在

    console.log(`📅 準備切換行程：${oldDate} ➝ ${newDate}`);

    if (hasUnsavedChanges.value) {
      console.log(`💾 正在儲存 ${oldDate} 的行程...`);
      try {
        await eventPlaceStore.saveItineraryToBackend(eventData.value.eventId, oldDate);
        console.log(`✅ ${oldDate} 行程儲存成功`);
        hasUnsavedChanges.value = false; // 成功儲存後重置
      } catch (error) {
        console.error(`❌ 無法儲存 ${oldDate} 行程`, error);
      }
    }
  },
  { immediate: false }
);

watch(
  () => [...itineraryForSelectedDay.value], // 解構陣列以確保 Vue 監聽變更
  (newValue, oldValue) => {
    if (JSON.stringify(newValue) !== JSON.stringify(oldValue)) {
      hasUnsavedChanges.value = true;
      console.log(`📝 行程修改 (包含拖曳變更): ${formattedSelectedDate.value}`);
    }
  },
  { deep: true }
);

// ------------- 離開時儲存 -------------
onBeforeRouteLeave(async (to, from, next) => {
  const date = formattedSelectedDate.value;

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
    await eventPlaceStore.saveItineraryToBackend(
      eventData.value.eventId,
      formattedSelectedDate.value
    );
    console.log("✅ 儲存完成");
    hasUnsavedChanges.value = false;
    itineraryStore.clearDayData(date);
    next();
  } catch (error) {
    console.error("❌ 儲存失敗", error.message || error);
    if (confirm("變更未儲存，是否仍要離開？")) {
      itineraryStore.clearDayData(date);
      console.log(`🗑️ 離開頁面，清除 ${date} 的資料`);
      next("/myitineraries"); // 🚀 正確導航方式
    } else {
      next(false);
    }
  }
});

// ------------- 瀏覽器關閉時提醒 -------------
const warnUnsavedChanges = (event) => {
  if (hasUnsavedChanges.value) {
    event.preventDefault();
    event.returnValue = "你有未儲存的變更，確定要離開嗎？";
    return "你有未儲存的變更，確定要離開嗎？";
  }
};

const handleVisibilityChange = () => {
  if (document.visibilityState === "hidden" && hasUnsavedChanges.value) {
    Swal.fire({
      title: "未儲存的變更",
      text: "你有未儲存的變更，確定要離開嗎？",
      icon: "warning",
      confirmButtonText: "知道了",
    });
  }
};

const handlePageHide = (event) => {
  if (hasUnsavedChanges.value) {
    event.preventDefault();
    event.returnValue = "你有未儲存的變更，確定要離開嗎？";
  }
};

onMounted(() => {
  window.addEventListener("beforeunload", warnUnsavedChanges);
  window.addEventListener("pagehide", handlePageHide);
  document.addEventListener("visibilitychange", handleVisibilityChange);
});

onUnmounted(() => {
  window.removeEventListener("beforeunload", warnUnsavedChanges);
  window.removeEventListener("pagehide", handlePageHide);
  document.removeEventListener("visibilitychange", handleVisibilityChange);
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
