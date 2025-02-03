<template>
  <div class="space-y-4">
    <h3 class="text-xl font-bold">{{ selectedDate }} 的行程</h3>

    <!-- 顯示當天的行程 -->
    <div v-if="itineraryForSelectedDay.length">
      <draggable
        v-model="itineraryForSelectedDay"
        :group="{ name: 'places', pull: 'clone', put: true }"
        :animation="200"
        item-key="id"
      >
        <!-- 包裹每個項目的 slot -->
        <template #item="{ element, index }">
          <ul class="space-y-4">
            <li
              class="bg-white p-4 rounded-xl shadow-lg border border-gray-200"
              :key="element.id"
            >
              <strong class="text-lg text-gray-800">{{ element.displayName }}</strong>
              <p class="text-gray-600">{{ element.formattedAddress }}</p>

              <!-- 刪除按鈕 -->
              <button class="text-red-500 mt-2 text-sm" @click="deletePlace(index)">
                刪除行程
              </button>
            </li>

            <!-- 顯示路徑時間 -->
            <div v-if="index < itineraryForSelectedDay.length - 1" class="mt-2">
              <route-time
                :origin="element"
                :destination="itineraryForSelectedDay[index + 1]"
                class="p-2 bg-gray-100 rounded-lg shadow-md"
              />
            </div>
          </ul>
        </template>
      </draggable>
    </div>
    <div v-else class="text-gray-500">
      <p>今天還沒有新增行程！</p>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import RouteTime from "./RouteTime.vue";
import draggable from "vuedraggable";

const props = defineProps({
  selectedDate: String,
});

const itineraryStore = useItineraryStore();

const itineraryForSelectedDay = computed({
  get: () => itineraryStore.getItineraryForDay(props.selectedDate),
  set: (newItinerary) => {
    itineraryStore.itineraryDates[props.selectedDate] = newItinerary;
  },
});

const deletePlace = (index) => {
  itineraryStore.removePlaceFromItinerary(props.selectedDate, index);
};

// 監聽 selectedDate 變化，更新行程顯示
watch(
  () => props.selectedDate,
  (newDate) => {
    console.log(`Selected date changed to: ${newDate}`);
  }
);

// 更新路徑時間
const updateRouteTimes = () => {
  console.log("Updating route times...");

  itineraryForSelectedDay.value.forEach((place, index) => {
    if (index < itineraryForSelectedDay.value.length - 1) {
      const nextPlace = itineraryForSelectedDay.value[index + 1];
      console.log(
        `Recalculate route time from ${place.displayName} to ${nextPlace.displayName}`
      );
      // 這裡可以加入重新計算路徑時間的邏輯
    }
  });
};
</script>

<style scoped>
li {
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  list-style-type: none;
}

.route-time {
  padding: 8px;
  background: #f7fafc;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 8px;
}

.draggable-item {
  cursor: move;
}

.draggable-placeholder {
  border: 2px dashed #ccc;
}
</style>
