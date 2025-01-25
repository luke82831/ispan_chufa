import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePlaceStore = defineStore('place', () => {
  const placeDetails = ref(null);
  const itineraries = ref([]);   // 儲存行程列表

  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

    // 加入行程
    const addToItinerary = (place) => {
      itineraries.value.push({
        displayName: place.displayName,
        formattedAddress: place.formattedAddress,
      });
    };
  
    // 刪除行程
    const removeFromItinerary = (index) => {
      itineraries.value.splice(index, 1);
    };

  return {
    placeDetails,
    setPlaceDetails,
    itineraries,
    addToItinerary,
    removeFromItinerary,
  };
});