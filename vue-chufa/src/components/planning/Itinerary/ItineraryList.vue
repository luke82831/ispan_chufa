<template>
  <div v-if="isExpanded && scheduleStore.currentSchedule" ref="popup" class="popup">
    <h2 class="title">{{ scheduleStore.currentSchedule.tripName }} 的所有行程</h2>

    <div class="event-list">
      <div
        v-for="event in scheduleStore.currentSchedule.events"
        :key="event.eventId"
        class="event-card"
      >
        <div class="event-header">
          <h3 class="event-date">📅 {{ event.date }}</h3>
          <p v-if="event.eventXPlaceBeans.length > 0" class="event-time">
            ⏰ {{ getStartTime(event.date) }} -
            {{ getEndTime(event.date) }}
          </p>
        </div>

        <ul v-if="event.eventXPlaceBeans.length > 0" class="place-list">
          <li
            v-for="(placeInfo, index) in computedItinerary(
              event.date,
              event.eventXPlaceBeans
            )"
            :key="index"
            class="place-card"
          >
            <span class="place-name"
              >📍
              {{
                placeInfo.placeName || `地點 ID: ${placeInfo.placeId} (載入中...)`
              }}</span
            >
            <p class="place-time">
              🕒 {{ formatTime(placeInfo.startTime) }} -
              {{ formatTime(placeInfo.endTime) }}
            </p>
            <p class="stay-time">
              ⏳ 停留時間:
              <strong>{{ formatDuration(placeInfo.stayDuration) }}</strong>
            </p>
          </li>
        </ul>

        <p v-else class="no-plan">📌 當天尚未安排行程</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, watch, onMounted, computed } from "vue";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { usePlaceStore } from "@/stores/placeStore";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { useRoute } from "vue-router";
import dayjs from "dayjs";
import duration from "dayjs/plugin/duration";

dayjs.extend(duration);

const route = useRoute();
const scheduleStore = useScheduleStore();
const placeStore = usePlaceStore();
const itineraryStore = useItineraryStore();

const props = defineProps({
  isExpanded: Boolean,
  selectedDate: String,
});

// 確保 `tripId` 只有在 `currentSchedule` 存在時才讀取
const tripId = computed(() => scheduleStore.currentSchedule?.tripId || null);

// 📌 監聽 `selectedDate` 變化，確保切換日期時行程同步
watch(
  () => scheduleStore.selectedDate,
  async (newDate) => {
    console.log(`🔄 [DEBUG] 切換日期 ${newDate}`);

    if (!newDate) {
      console.warn(`⚠️ [DEBUG] selectedDate 是 null`);
      return;
    }

    if (!scheduleStore.currentSchedule) {
      console.warn(`⚠️ [DEBUG] currentSchedule 尚未載入`);
      return;
    }

    await scheduleStore.fetchScheduleById(scheduleStore.currentSchedule?.tripId);
    console.log(`✅ [DEBUG] 已載入 ${newDate} 的行程`);
  },
  { immediate: true }
);

onMounted(async () => {
  if (scheduleStore.selectedDate && tripId.value) {
    console.log("📌 初次加載行程，日期:", scheduleStore.selectedDate);
    await scheduleStore.fetchScheduleById(tripId.value);
  }
});

// 📌 計算每天的行程時間
const computedItinerary = (date, itinerary) => {
  console.log(`📅 [DEBUG] 計算 ${date} 的行程`);

  if (!itinerary || itinerary.length === 0) {
    console.warn(`⚠️ [DEBUG] ${date} 沒有行程`);
    return [];
  }

  let [year, month, day] = date.split("-").map(Number);
  let startTimeStr = getStartTime(date);

  if (!startTimeStr || startTimeStr === "Invalid Date") {
    console.error(`❌ [ERROR] 日期 ${date} 沒有正確的 startTime`);
    return [];
  }

  console.log(`🕒 [DEBUG] 計算 ${date} 的 startTime = ${startTimeStr}`);

  let [hours, minutes] = startTimeStr.split(":").map(Number);
  if (isNaN(hours) || isNaN(minutes)) {
    console.error(`❌ [ERROR] 無法解析 ${date} 的 startTime: ${startTimeStr}`);
    return [];
  }

  let baseTime = new Date(year, month - 1, day, hours, minutes);
  console.log(`🕒 [DEBUG] baseTime (${date}) =`, baseTime);

  return itinerary.map((place, index) => {
    let travelTime = index > 0 ? itineraryStore.routeTimes[date]?.[index - 1] || 0 : 0;
    let stayTime = itineraryStore.stayDurations[date]?.[index] ?? 0;

    let startTime = new Date(baseTime.getTime());
    if (index > 0) {
      startTime = new Date(itineraryWithTimes[index - 1].endTime);
      startTime.setMinutes(startTime.getMinutes() + travelTime);
    }

    let endTime = new Date(startTime.getTime());
    endTime.setMinutes(endTime.getMinutes() + stayTime);

    console.log(
      `📌 [DEBUG] ${date} 地點 ${index} (${place.placeName}) - startTime: ${startTime}, endTime: ${endTime}`
    );

    return {
      placeId: place.placeId,
      placeName: getPlaceName(place.placeId),
      stayDuration: stayTime,
      startTime,
      endTime,
    };
  });
};

// 📌 修正格式化停留時間
const formatDuration = (duration) => {
  if (!duration) return "無停留";

  let durationInMinutes =
    duration >= 60 ? Math.round(duration / 60) : Math.round(duration);
  return durationInMinutes > 0 ? `${durationInMinutes} 分鐘` : "無停留";
};

// 📌 格式化時間（08:00:00 → 08:00）
const formatTime = (time) => {
  if (!time) return "尚未設定"; // 處理 `undefined` 或 `null`

  if (typeof time === "string") {
    if (time.match(/^\d{2}:\d{2}:\d{2}$/)) {
      return dayjs(time, "HH:mm:ss").format("HH:mm"); // 轉換 `08:00:00` → `08:00`
    }
    if (time.match(/^\d{2}:\d{2}$/)) {
      return time; // 已經是 `HH:mm`，直接返回
    }
  } else if (time instanceof Date) {
    return dayjs(time).format("HH:mm");
  }

  return "尚未設定"; // 預防錯誤
};

// 📌 透過 `placeId` 取得 `placeName`
const getPlaceName = (placeId) => {
  return placeStore.getPlaceDetailById(placeId)?.placeName || null;
};

// 📌 取得每天的 `startTime` 和 `endTime`
const getStartTime = (date) => {
  const startTime = itineraryStore.startTimes[date];
  console.log(`🕒 [DEBUG] getStartTime(${date}) =`, startTime);

  if (!startTime) {
    console.warn(`⚠️ 日期 ${date} 沒有設定出發時間`);
    return "Invalid Date";
  }
  return startTime;
};

const getEndTime = (date) => {
  const endTime = itineraryStore.endTimes[date];
  console.log(`⏳ [DEBUG] getEndTime(${date}) =`, endTime);

  if (!endTime) {
    console.warn(`⚠️ 日期 ${date} 沒有設定結束時間`);
    return "Invalid Date";
  }
  return endTime;
};
</script>

<style scoped>
/* 整體彈出視窗樣式 */
.popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 500px;
  max-width: 50%;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.15);
  padding: 20px;
  overflow-y: auto;
  max-height: 80vh;
  z-index: 1000000;
}

/* 標題樣式 */
.title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 15px;
}

/* 行程列表 */
.event-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 單日行程卡片 */
.event-card {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

/* 日期 + 時間 */
.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-date {
  font-size: 1.2rem;
  font-weight: bold;
  color: #007aff;
}

.event-time {
  font-size: 0.9rem;
  color: #555;
}

/* 地點列表 */
.place-list {
  list-style: none; /* 移除點點 */
  padding: 0; /* 取消縮進 */
}

/* 讓地點資訊更清楚 */
.place-card {
  background: #ffffff;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
}

/* 地點名稱 */
.place-name {
  font-weight: bold;
  font-size: 1rem;
  color: #333;
}

/* 時間顯示 */
.place-time {
  font-size: 0.9rem;
  color: #666;
}

.stay-time {
  font-size: 0.9rem;
  color: #999;
}

/* 無行程時的提示 */
.no-plan {
  text-align: center;
  font-size: 1rem;
  color: #ff3b30;
  font-weight: bold;
  margin-top: 10px;
}
</style>
