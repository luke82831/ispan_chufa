import { defineStore } from "pinia";

export const useItineraryStore = defineStore("itinerary", {
  state: () => ({
    itineraryDates: {},
    startTimes: {}, // 存放每一天的出發時間
  }),

  getters: {
    getItineraryForDay: (state) => (date) => {
      return state.itineraryDates[date] ?? [];
    },
    getStartTime: (state) => (date) => {
      return state.startTimes[date] ?? "08:00"; // 確保有預設出發時間
    },
  },

  actions: {
    // 初始化行程（從後端載入時使用）
    setItinerary(date, itinerary) {
      this.itineraryDates[date] = Array.isArray(itinerary) ? itinerary : []; // 確保存入的是陣列
    },

    setStartTime(date, startTime) {
      if (!this.startTimes) this.startTimes = {};
      this.startTimes[date] = startTime;
    },

    // **前端新增景點**
    addPlace(date, place) {
      if (!this.itineraryDates[date]) {
        this.itineraryDates[date] = [];
      }
      this.itineraryDates[date].push(place);
    },

    // **前端刪除景點**
    removePlace(date, index) {
      if (this.itineraryDates[date]) {
        this.itineraryDates[date].splice(index, 1);
      }
    },

    // **前端拖曳排序**
    updateOrder(date, newOrder) {
      this.itineraryDates[date] = newOrder;
    },
  },
});
