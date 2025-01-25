import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePlaceStore = defineStore('place', () => {
  const placeDetails = ref(null);
  const itineraries = ref([]); // 儲存行程列表

  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

  // 加入行程
  const addToItinerary = (place) => {
    // 檢查是否已有相同的行程
    const existingPlace = itineraries.value.find(
      (itinerary) => itinerary.displayName === place.displayName && itinerary.formattedAddress === place.formattedAddress
    );

    if (!existingPlace) {
      itineraries.value.push({
        displayName: place.displayName,
        formattedAddress: place.formattedAddress,
      });
    } else {
      console.log("這個行程已經存在，不會重複加入");
    }
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
