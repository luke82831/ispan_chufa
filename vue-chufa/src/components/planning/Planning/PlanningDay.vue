<template>
  <div>
    <h3>{{ selectedDate }} 的行程</h3>

    <!-- 顯示當天的行程 -->
    <div v-if="itineraryForSelectedDay.length">
      <ul>
        <li v-for="(place, index) in itineraryForSelectedDay" :key="place.displayName">
          <strong>{{ place.displayName }}</strong>
          <p>{{ place.formattedAddress }}</p>

          <!-- 顯示當前地點和下一個地點之間的路徑時間 -->
          <div v-if="index < itineraryForSelectedDay.length - 1">
            <route-time
              :origin="place"
              :destination="itineraryForSelectedDay[index + 1]"
            />
          </div>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>今天還沒有新增行程！</p>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import RouteTime from "./RouteTime.vue";

// 接收父組件傳遞的 selectedDate
const props = defineProps({
  selectedDate: String,
});

const itineraryStore = useItineraryStore();

// 計算選擇日期的行程
const itineraryForSelectedDay = computed(() => {
  return itineraryStore.getItineraryForDay(props.selectedDate);
});

// 監聽 selectedDate 變化，更新行程顯示
watch(
  () => props.selectedDate,
  (newDate) => {
    console.log(`Selected date changed to: ${newDate}`);
  }
);
</script>
