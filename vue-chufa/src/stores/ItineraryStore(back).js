import { defineStore } from "pinia";
import { v4 as uuidv4 } from "uuid"; // 引入 uuid 库

export const useItineraryStore = defineStore("itinerary", {
  state: () => ({
    itineraryTitle: "",
    startDate: "",
    endDate: "",
    selectedDate: "",
    userId: null,
    coverPhoto: null, // 存封面圖片
    itineraryDates: {}, // 每一天的行程資料，鍵是日期，值是行程細節
    routeTimes: {}, // 存儲行車時間
    stayDurations: {}, // 存儲每個地點的停留時間
    itineraries: [],
  }),

  actions: {
    setItinerary(title, start, end) {
      this.itineraryTitle = title;
      this.startDate = start;
      this.endDate = end;
    },

    setSelectedDate(date) {
      this.selectedDate = date;
    },

    addItineraryDate(date) {
      const newDate = new Date(date);
      if (!this.endDate || newDate > this.endDate) {
        this.endDate = newDate;
      }
    },

    // 新增某個日期的行程
    addPlaceToDay(date, place) {
      // 為每個地點生成唯一的 ID
      const placeWithId = { ...place, id: uuidv4() };

      if (!this.itineraryDates[date]) {
        this.itineraryDates[date] = []; // 如果該日期尚未有行程，先初始化為空陣列
      }
      this.itineraryDates[date].push(placeWithId); // 新增行程
    },

    // 取得某個日期的行程
    getItineraryForDay(date) {
      return this.itineraryDates[date] || []; // 如果該日期沒有行程，回傳空陣列
    },

    removePlaceFromItinerary(date, index) {
      // 確保 itineraryDates[date] 不為 undefined
      if (!this.itineraryDates[date]) {
        console.warn(`No itinerary found for date: ${date}`);
        return;
      }

      // 確保 index 是有效的
      if (index < 0 || index >= this.itineraryDates[date].length) {
        console.warn("Invalid index");
        return;
      }

      // 根據 index 刪除對應的地點
      this.itineraryDates[date].splice(index, 1); // 刪除單一地點
    },

    // 新增 updateRouteTimesForDay 方法，用來更新該天的路徑時間結果
    updateRouteTimesForDay(selectedDate, routeTimes) {
      this.routeTimes[selectedDate] = routeTimes;
    },

    // 新增 setRouteTime 方法，用來儲存行車時間
    setRouteTime(date, index, time) {
      if (!this.routeTimes[date]) {
        this.routeTimes[date] = {};
      }
      console.log(
        `⏳ 接收到路程時間：${date} | Index: ${index} | Duration: ${time}`
      );
      this.routeTimes[date][index] = time;
    },

    // 新增 StayDuration 方法，用來儲存停留時間
    setStayDuration(date, index, duration) {
      if (!this.stayDurations[date]) {
        this.stayDurations[date] = {};
      }
      this.stayDurations[date][index] = duration;

      console.log(
        `⏳ 設定停留時間：${date} | Index: ${index} | Duration: ${duration}`
      );
    },

    getStayDuration(date, placeId) {
      return this.stayDurations[date]?.[placeId] || 0; // 預設 0 分鐘
    },
  },
});
