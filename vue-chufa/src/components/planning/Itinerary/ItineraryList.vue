<template>
  <div class="overview-container">
    <h2>📌 行程總覽</h2>

    <div v-if="sortedEvents.length === 0" class="no-itinerary">
      <p>📌 目前沒有任何行程安排</p>
    </div>

    <div v-else class="event-list">
      <div v-for="(day, index) in sortedEvents" :key="index" class="day-card">
        <div class="day-header">
          <h3>📅 {{ day.date }}</h3>
          <span v-if="day.events.length > 0" class="time-range">
            🕒 {{ itineraryStore.getStartTime(day.date) }} -
            {{ itineraryStore.getEndTime(day.date) }}
          </span>
        </div>

        <div v-if="day.events.length > 0" class="event-details">
          <ul>
            <li v-for="(event, idx) in day.events" :key="idx">
              <div class="event-info">
                <span class="order">#{{ event.index + 1 }}</span>
                <strong class="place-name">{{ event.placeName }}</strong>
                <span class="place-address">{{ event.placeAddress }}</span>
              </div>
              <span class="stay-time">
                ⏳ 停留時間:
                {{
                  formatStayTime(
                    itineraryStore.getStayDuration(day.date, event.index)
                  )
                }}
              </span>
            </li>
          </ul>
        </div>

        <!-- 📌 本日無安排行程 -->
        <div v-else class="no-schedule">📌 本日無安排行程</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { onBeforeRouteLeave } from "vue-router";
import { useItineraryStore } from "@/stores/ItineraryStore";

const itineraryStore = useItineraryStore();

// 取得所有行程事件，並整理成按照日期排序的格式
const sortedEvents = computed(() => {
  let eventsByDate = [];

  console.log("📅 目前的行程數據:", itineraryStore.itineraryDates);

  for (const date in itineraryStore.itineraryDates) {
    const dayEvents = itineraryStore.itineraryDates[date].map((event) => ({
      ...event,
      date, // 添加日期屬性
    }));

    eventsByDate.push({
      date,
      events: dayEvents.sort((a, b) => a.index - b.index), // 按地點順序排列
    });
  }

  return eventsByDate.sort((a, b) => new Date(a.date) - new Date(b.date));
});

// **格式化停留時間**
const formatStayTime = (minutes) => {
  if (!minutes || minutes <= 0) return "0 分鐘";
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return hours > 0 ? `${hours} 小時 ${mins} 分鐘` : `${mins} 分鐘`;
};

/** 🔥 離開行程列表時，清除所有日期的行程數據 */
onBeforeRouteLeave((to, from, next) => {
  console.log("🛑 準備離開 OverviewPage.vue，10 秒後清除所有行程數據...");

  setTimeout(() => {
    if (to.fullPath !== from.fullPath) {
      console.log("🗑️ 確認使用者真的離開，清除所有行程");
      itineraryStore.clearAllData(); // ✅ 清空所有行程
    }
  }, 5000); // 5 秒內如果回來，數據不會被清除

  next();
});
</script>

<style scoped>
/* 整體頁面樣式 */
.overview-container {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* 無行程時的顯示 */
.no-itinerary {
  text-align: center;
  font-size: 18px;
  padding: 20px;
  color: #777;
  background: #f2f2f2;
  border-radius: 8px;
}

/* 行程卡片樣式 */
.day-card {
  background: white;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

/* 行程標題（日期） */
.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 2px solid #ddd;
  padding-bottom: 8px;
  margin-bottom: 10px;
}

/* 時間區間 */
.time-range {
  font-size: 14px;
  color: #555;
  font-weight: normal;
}

/* 行程細節區塊 */
.event-details ul {
  list-style-type: none;
  padding: 0;
}

.event-details li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

/* 地點資訊 */
.event-info {
  display: flex;
  flex-direction: column;
}

.place-name {
  font-size: 16px;
  font-weight: bold;
  color: #007bff;
}

.place-address {
  font-size: 14px;
  color: #777;
}

/* 編號樣式 */
.order {
  font-weight: bold;
  color: #ff6b6b;
  margin-right: 5px;
}

/* 停留時間 */
.stay-time {
  font-size: 14px;
  color: #ff8c00;
}

/* 無行程時的區塊 */
.no-schedule {
  text-align: center;
  font-size: 16px;
  color: #777;
  background: #f8f9fa;
  padding: 10px;
  border-radius: 5px;
}
</style>
