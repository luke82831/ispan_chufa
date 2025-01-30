import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePlaceStore = defineStore('place', () => {
  const placeDetails = ref(null);
  const itineraries = ref([]); // 儲存行程列表
  const selectedDate = ref(null); // 儲存當前選擇的日期
  const itinerariesByDate = ref({}); // 儲存每個日期的行程

  const origin = ref(null);  // 儲存起點
  const destination = ref(null);  // 儲存終點

  const setPlaceDetails = (details) => {
    placeDetails.value = details;
  };

  const setOrigin = (place) => {
    origin.value = place;
    // console.log("Origin:", JSON.stringify(origin.value, null, 2));
  };

  const setDestination = (place) => {
    destination.value = place;
    // console.log("Destination:", JSON.stringify(destination.value, null, 2));
  };

  const setSelectedDate = (date) => {
    selectedDate.value = date;
  };

  const addToItinerary = async (place) => {
    if (!selectedDate.value) {
      console.error("請選擇一個日期再加入行程");
      return;
    }

    const dateKey = selectedDate.value;

    if (!itinerariesByDate.value[dateKey]) {
      itinerariesByDate.value[dateKey] = [];
    }

    const itinerary = itinerariesByDate.value[dateKey];

    // 新增地點
    itinerary.push({
      type: 'place',
      displayName: place.displayName,
      formattedAddress: place.formattedAddress,
      lat: place.location.lat,
      lng: place.location.lng,
    });

    console.log(`地點已加入到 ${dateKey} 行程`, itinerariesByDate.value[dateKey]);
  };

  const removeFromItinerary = (index, date) => {
    if (itinerariesByDate.value[date]) {
      itinerariesByDate.value[date].splice(index, 1);
    }
  };

  const reset = () => {
    placeDetails.value = null;
    itineraries.value = [];
    selectedDate.value = null;
    itinerariesByDate.value = {};
    origin.value = null;
    destination.value = null;
  };

  return {
    placeDetails,
    selectedDate,
    itineraries,
    itinerariesByDate,
    origin,
    destination,
    setPlaceDetails,
    setSelectedDate,
    addToItinerary,
    removeFromItinerary,
    setOrigin,
    setDestination,
    reset,
  };
});
