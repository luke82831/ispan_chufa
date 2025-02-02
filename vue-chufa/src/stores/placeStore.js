import { defineStore } from "pinia";
import { ref } from "vue";
import { useItineraryStore } from "./ItineraryStore"; // 引入 ItineraryStore

export const usePlaceStore = defineStore("place", () => {
  const placeDetails = ref(null);
  const itineraries = ref([]); // 儲存行程列表
  const origin = ref(null);  // 儲存起點
  const destination = ref(null);  // 儲存終點

  const itineraryStore = useItineraryStore(); // 使用 ItineraryStore

  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

  const setOrigin = (place) => {
    origin.value = place;
    console.log("origin", origin.value);
  };

  const setDestination = (place) => {
    destination.value = place;
    console.log("destination", destination.value);
  };

  const addToItinerary = async (place) => {
    // 使用 ItineraryStore 來處理行程加入
    itineraryStore.addPlaceToDay(itineraryStore.selectedDate, place);
    console.log("地點已加入行程:", place);
  };

  return {
    placeDetails,
    itineraries,
    origin,
    destination,
    setPlaceDetails,
    setOrigin,
    setDestination,
    addToItinerary,
  };
});
