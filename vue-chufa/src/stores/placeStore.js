import { defineStore } from "pinia";
import { ref } from "vue";
import { useItineraryStore } from "./ItineraryStore"; // 引入 ItineraryStore

export const usePlaceStore = defineStore("place", () => {
  const placeDetails = ref(null);
  const itineraries = ref([]); // 儲存行程列表
  const routePairs = ref({}); // 存放起點與終點的配對，格式 { date: { index: { origin, destination } } }

  const itineraryStore = useItineraryStore(); // 使用 ItineraryStore

  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

  const updateRoutePair = (date, index, origin, destination) => {
    if (!routePairs.value[date]) {
      routePairs.value[date] = {};
    }
    routePairs.value[date][index] = { origin, destination };

    // console.log(`📌 updateRoutePair 更新: ${date} - ${index}`);
    // console.log(
    //   "🔍 當前 routePairs:",
    //   JSON.stringify(routePairs.value, null, 2)
    // );
  };

  const addToItinerary = async (place) => {
    itineraryStore.addPlaceToDay(itineraryStore.selectedDate, place);
    console.log("地點已加入行程:", place);
  };

  return {
    placeDetails,
    itineraries,
    routePairs,
    setPlaceDetails,
    updateRoutePair,
    addToItinerary,
  };
});
