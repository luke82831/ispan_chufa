import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePlaceStore = defineStore('place', () => {
  const placeDetails = ref(null);
  const selectedPlace = ref(null); // 用來保存選擇的地點


  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

  const setSelectedPlace = (place) => {
    selectedPlace.value = place;
  };

  return {
    placeDetails,
    setPlaceDetails,
    selectedPlace,
    setSelectedPlace, // 用來更新 selectedPlace 的方法
  };
});