import { defineStore } from "pinia";

export const useItineraryStore = defineStore("itinerary", {
  state: () => ({
    itineraryDates: {},
    startTimes: {},   // 存放每一天的出發時間
    routeTimes: {},   // 存放每個行程的行車時間 (以 index 為 key)
    stayDurations: {},// 存放停留時間 (以 index 為 key)
    isEditingStays: {},
    tempStayDurations: {},
  }),

  getters: {
    getItineraryForDay: (state) => (date) => {
      return state.itineraryDates[date] ?? [];
    },
    getStartTime: (state) => (date) => {
      return state.startTimes[date] ?? "08:00"; // 確保有預設出發時間
    },
    getStayDuration: (state) => (date, index) => {
      return state.stayDurations[date]?.[index] ?? 0;
    },

    // 🔥 Getter：讀取「是否正在編輯」
    getIsEditingStay: (state) => (date, index) => {
      return state.isEditingStays[date]?.[index] ?? false;
    },

    // 🔥 Getter：讀取「暫存停留時間」
    getTempStayDuration: (state) => (date, index) => {
      return state.tempStayDurations[date]?.[index] ?? 0;
    },

    getRoutePairs: (state) => (date) => {
      const places = state.itineraryDates[date] ?? [];
      let routePairs = {};
      for (let i = 0; i < places.length - 1; i++) {
        routePairs[i] = {
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
    // **從後端載入行程**
    setItinerary(date, itinerary) {
      if (!Array.isArray(itinerary)) {
        console.warn(`setItinerary: 預期收到陣列，但收到 ${typeof itinerary}`);
        itinerary = [];
      }

      // ✅ 改用 index，統一管理順序
      const normalizedItinerary = itinerary
        .filter((place) => place !== null && place !== undefined)
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

    setStayDuration(date, index, duration) {
      if (!this.stayDurations[date]) {
        this.stayDurations[date] = {};
      }
      this.stayDurations[date][index] = duration;
    },

    // 🔥 Action：設定「是否正在編輯」
    setIsEditingStay(date, index, isEditing) {
      if (!this.isEditingStays[date]) {
        this.isEditingStays[date] = {};
      }
      this.isEditingStays[date][index] = isEditing;
    },

    // 🔥 Action：設定「暫存停留時間」
    setTempStayDuration(date, index, tempDuration) {
      if (!this.tempStayDurations[date]) {
        this.tempStayDurations[date] = {};
      }
      this.tempStayDurations[date][index] = tempDuration;
    },

    // **前端刪除景點**
    removePlace(date, index) {
      if (
        this.itineraryDates[date] &&
        index >= 0 &&
        index < this.itineraryDates[date].length
      ) {
        // 🔥 移除景點
        this.itineraryDates[date].splice(index, 1);

        // 🔥 重新計算 `index`
        this.itineraryDates[date] = this.itineraryDates[date].map(
          (place, newIndex) => ({
            ...place,
            index: newIndex, // 重新排列 index，確保連續
          })
        );

        // 🔥 同步更新 routeTimes & stayDurations
        if (this.routeTimes[date]) {
          const updatedRouteTimes = {};
          Object.keys(this.routeTimes[date]).forEach((key) => {
            const newKey =
              parseInt(key) > index ? parseInt(key) - 1 : parseInt(key);
            if (newKey >= 0)
              updatedRouteTimes[newKey] = this.routeTimes[date][key];
          });
          this.routeTimes[date] = updatedRouteTimes;
        }

        if (this.stayDurations[date]) {
          const updatedStayDurations = {};
          Object.keys(this.stayDurations[date]).forEach((key) => {
            const newKey =
              parseInt(key) > index ? parseInt(key) - 1 : parseInt(key);
            if (newKey >= 0)
              updatedStayDurations[newKey] = this.stayDurations[date][key];
          });
          this.stayDurations[date] = updatedStayDurations;
        }

        // 🔥 同步更新 isEditingStays & tempStayDurations
        if (this.isEditingStays[date]) {
          const updatedEditing = {};
          Object.keys(this.isEditingStays[date]).forEach((key) => {
            const newKey =
              parseInt(key) > index ? parseInt(key) - 1 : parseInt(key);
            if (newKey >= 0)
              updatedEditing[newKey] = this.isEditingStays[date][key];
          });
          this.isEditingStays[date] = updatedEditing;
        }

        if (this.tempStayDurations[date]) {
          const updatedTemps = {};
          Object.keys(this.tempStayDurations[date]).forEach((key) => {
            const newKey =
              parseInt(key) > index ? parseInt(key) - 1 : parseInt(key);
            if (newKey >= 0)
              updatedTemps[newKey] = this.tempStayDurations[date][key];
          });
          this.tempStayDurations[date] = updatedTemps;
        }
      } else {
        console.warn(`removePlace: 無效的索引 ${index}`);
      }
    },

    // **前端拖曳排序**
    updateOrder(date, newOrder) {
      // ✅ 重新設定 index，確保順序正確
      this.itineraryDates[date] = newOrder.map((place, index) => ({
        ...place,
        index: index, // 重新計算 index
      }));

      // ✅ 同步調整 `routeTimes` & `stayDurations`
      const updatedRouteTimes = {};
      Object.keys(this.routeTimes[date] || {}).forEach((oldIndex) => {
        const newIndex = newOrder.findIndex(
          (p) => p.index === parseInt(oldIndex)
        );
        if (newIndex !== -1)
          updatedRouteTimes[newIndex] = this.routeTimes[date][oldIndex];
      });
      this.routeTimes[date] = updatedRouteTimes;

      const updatedStayDurations = {};
      Object.keys(this.stayDurations[date] || {}).forEach((oldIndex) => {
        const newIndex = newOrder.findIndex(
          (p) => p.index === parseInt(oldIndex)
        );
        if (newIndex !== -1)
          updatedStayDurations[newIndex] = this.stayDurations[date][oldIndex];
      });
      this.stayDurations[date] = updatedStayDurations;

      // 🔥 同步調整 isEditingStays & tempStayDurations
      const updatedEditing = {};
      Object.keys(this.isEditingStays[date] || {}).forEach((oldIndex) => {
        const newIndex = newOrder.findIndex(
          (p) => p.index === parseInt(oldIndex)
        );
        if (newIndex !== -1)
          updatedEditing[newIndex] = this.isEditingStays[date][oldIndex];
      });
      this.isEditingStays[date] = updatedEditing;

      const updatedTemps = {};
      Object.keys(this.tempStayDurations[date] || {}).forEach((oldIndex) => {
        const newIndex = newOrder.findIndex(
          (p) => p.index === parseInt(oldIndex)
        );
        if (newIndex !== -1)
          updatedTemps[newIndex] = this.tempStayDurations[date][oldIndex];
      });
      this.tempStayDurations[date] = updatedTemps;
    },
  },
});
