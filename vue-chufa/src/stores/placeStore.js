import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePlaceStore = defineStore('place', () => {
  const placeDetails = ref(null);
  const itineraries = ref([]); // 儲存行程列表
  const selectedDate = ref(null); // 儲存當前選擇的日期
  const itinerariesByDate = ref({}); // 儲存每個日期的行程

  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

  const addToItinerary = (place) => {
    if (!selectedDate.value) {
      console.error("請選擇一個日期再加入行程");
      return;
    }

    if (!itinerariesByDate.value[selectedDate.value]) {
      itinerariesByDate.value[selectedDate.value] = [];
    }

    itinerariesByDate.value[selectedDate.value].push({
      displayName: place.displayName,
      formattedAddress: place.formattedAddress,
    });
    console.log(`地點已加入到 ${selectedDate.value} 行程`, itinerariesByDate.value[selectedDate.value]);
  };

  const removeFromItinerary = (index, date) => {
    if (itinerariesByDate.value[date]) {
      itinerariesByDate.value[date].splice(index, 1);
    }
  };

  const setSelectedDate = (date) => {
    selectedDate.value = date;
  };

  return {
    placeDetails,
    setPlaceDetails,
    itineraries,
    addToItinerary,
    removeFromItinerary,
    selectedDate,
    itinerariesByDate,
    setSelectedDate,  // 新增的 setSelectedDate 方法
  };
});
