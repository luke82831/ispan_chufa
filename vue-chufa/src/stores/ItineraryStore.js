import { defineStore } from "pinia";

export const useItineraryStore = defineStore("itinerary", {
  state: () => ({
    itineraryDates: {},
    startTimes: {}, // 存放每一天的出發時間
    routeTimes: {}, // 存放每個行程的行車時間
    stayDurations: {}, // 存放停留時間
  }),

  getters: {
    getItineraryForDay: (state) => (date) => {
      return state.itineraryDates[date] ?? [];
    },
    getStartTime: (state) => (date) => {
      return state.startTimes[date] ?? "08:00"; // 確保有預設出發時間
    },
    getStayDuration: (state) => (date, placeId) => {
      return state.stayDurations[date]?.[placeId] ?? 0; // 預設 0 分鐘
    },

    getRoutePairs: (state) => (date) => {
      const places = state.itineraryDates[date] ?? [];

      let routePairs = {};
      for (let i = 0; i < places.length - 1; i++) {
        routePairs[i] = {
          // ✅ 直接用 index 作為 key
          origin: { lat: places[i].latitude, lng: places[i].longitude },
          destination: {
            lat: places[i + 1].latitude,
            lng: places[i + 1].longitude,
          },
        };
      }
      return routePairs;
    },
  },

  actions: {
    // 從後端載入行程
    setItinerary(date, itinerary) {
      if (!Array.isArray(itinerary)) {
        console.warn(`setItinerary: 預期收到陣列，但收到 ${typeof itinerary}`);
        itinerary = [];
      }

      // 🔥 只使用 index，完全不考慮 placeOrder
      const normalizedItinerary = itinerary
        .filter((place) => place !== null && place !== undefined) // 過濾掉 undefined
        .map((place, index) => ({
          placeId: place.placeId ?? null,
          placeName: place.placeName ?? "",
          placeAddress: place.placeAddress ?? "",
          latitude: place.latitude ?? null,
          longitude: place.longitude ?? null,
          index: index, // ✅ 改成 index
          travelTime: place.travelTime ?? null,
          stayDuration: place.stayDuration ?? null,
          notes: place.notes ?? null,
          photos: Array.isArray(place.photos) ? place.photos : [],
        }));

      console.log("🚀 更新行程資料 (使用 index):", normalizedItinerary);
      this.itineraryDates[date] = normalizedItinerary;
    },

    setStartTime(date, startTime) {
      this.startTimes[date] = startTime;
    },

    setRouteTime(date, index, time) {
      if (!this.routeTimes[date]) {
        this.routeTimes[date] = {};
      }
      this.routeTimes[date][index] = time;
    },

    setStayDuration(date, placeId, duration) {
      if (!this.stayDurations[date]) {
        this.stayDurations[date] = {};
      }
      this.stayDurations[date][placeId] = duration;
    },

    // **前端刪除景點**
    removePlace(date, index) {
      if (
        this.itineraryDates[date] &&
        index >= 0 &&
        index < this.itineraryDates[date].length
      ) {
        this.itineraryDates[date].splice(index, 1);
      } else {
        console.warn(
          `removePlace: 無效的索引 ${index}，當前陣列長度: ${
            this.itineraryDates[date]?.length ?? 0
          }`
        );
      }
    },

    // **前端拖曳排序**
    updateOrder(date, newOrder) {
      this.itineraryDates[date] = [...newOrder];
    },
  },
});
