<template>
  <div class="space-y-4">
    <h3 class="text-xl font-bold">{{ selectedDate }} 的行程</h3>

    <!-- 設定出發時間 -->
    <div class="flex items-center space-x-2">
      <label class="font-bold">出發時間：</label>
      <input type="time" v-model="departureTime" class="border p-1 w-24" />
    </div>

    <!-- 列印經緯度資料按鈕 -->
    <!-- <button
      @click="logRouteCoordinates"
      class="bg-blue-500 text-white py-1 px-3 rounded mb-4"
    >
      列出所有經緯度資料
    </button> -->

    <!-- 顯示當天的行程 -->
    <div v-if="itineraryForSelectedDay.length">
      <draggable
        v-model="itineraryForSelectedDay"
        :group="{ name: 'places', pull: 'clone', put: true }"
        :animation="200"
        item-key="id"
        @end="handleDragEnd"
      >
        <template #item="{ element, index }">
          <ul class="space-y-4">
            <li
              class="bg-white p-4 rounded-xl shadow-lg border border-gray-200"
              :key="element.id"
            >
              <strong class="text-lg text-gray-800">{{
                element.displayName
              }}</strong>
              <p class="text-gray-600">{{ element.formattedAddress }}</p>

              <!-- 設定停留時間（點擊進入編輯模式） -->
              <div class="flex items-center space-x-2 mt-2">
                <label>停留時間：</label>

                <!-- 顯示超連結模式 -->
                <a
                  v-if="!element.isEditingStay"
                  href="#"
                  @click.prevent="editStayTime(element)"
                  class="text-blue-500 underline"
                >
                  {{ element.stayDuration }} 分
                </a>

                <!-- 編輯模式 -->
                <input
                  v-else
                  type="number"
                  v-model="element.tempStayDuration"
                  class="border p-1 w-16"
                  @blur="saveStayTime(element)"
                  @keyup.enter="saveStayTime(element)"
                />
              </div>

              <!-- 刪除按鈕 -->
              <button
                class="text-red-500 mt-2 text-sm"
                @click="deletePlace(index)"
              >
                刪除行程
              </button>
            </li>

            <!-- 顯示路徑時間 -->
            <div v-if="index < itineraryForSelectedDay.length - 1" class="mt-2">
              <route-time
                :date="selectedDate"
                :index="index"
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
    <!-- 傳遞到 staytime 組件 -->
    <StayTime
      :departureTime="departureTime"
      :itinerary="itineraryForSelectedDay"
    />
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
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

const itineraryForSelectedDay = computed({
  get: () => itineraryStore.getItineraryForDay(props.selectedDate),
  set: (newItinerary) => {
    itineraryStore.itineraryDates[props.selectedDate] = newItinerary;
  },
});

// **處理地點刪除後更新 routePairs**
const deletePlace = (index) => {
  console.log(`🗑 刪除行程: ${index}`);

  itineraryStore.removePlaceFromItinerary(props.selectedDate, index);

  // **確保刪除後的 routePairs 正確更新**
  updateRoutePairs();
};

// **更新地點順序時，更新 placeStore.routePairs**
const handleDragEnd = () => {
  // console.log("🛠 拖曳結束，更新 placeStore.routePairs");
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

  // console.log(
  //   "🔄 更新後的 routePairs:",
  //   JSON.stringify(placeStore.routePairs, null, 2)
  // );
};

// **監聽 routePairs 的變化，確保時間重新計算**
watch(
  () => placeStore.routePairs[props.selectedDate],
  (newVal) => {
    // console.log(
    //   `🔍 監聽到 routePairs 變更:`,
    //   JSON.stringify(placeStore.routePairs, null, 2)
    // );
    if (newVal) {
      console.log("✅ 觸發計算，開始更新路徑時間");
    } else {
      // console.warn("⚠️ newVal 為空，未能觸發計算");
    }
  },
  { immediate: true, deep: true }
);

// 時間相關 //

// **進入編輯模式**
const editStayTime = (place) => {
  place.isEditingStay = true; // 開啟編輯模式
  place.tempStayDuration = place.stayDuration; // 暫存原本的值
};

// **儲存新值**
const saveStayTime = (place) => {
  place.stayDuration = Number(place.tempStayDuration); // 更新值
  place.isEditingStay = false; // 退出編輯模式
};

// **列印所有經緯度資料到 Console**
// const logRouteCoordinates = () => {
//   console.log("📍 當前行程經緯度：", itineraryForSelectedDay.value);
// };
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
