import { defineStore } from "pinia";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { usePlaceStore } from "@/stores/placeStore";

export const useItineraryStore = defineStore("itinerary", {
  state: () => ({
    itineraryDates: {},
    startTimes: {}, // 存放每一天的出發時間
    endTimes: {}, // 存放每一天的結束時間
    routeTimes: {}, // 存放每個行程的行車時間 (以 index 為 key)
    stayDurations: {}, // 存放停留時間 (以 index 為 key)
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

    getEndTime: (state) => (date) => {
      return state.endTimes[date] ?? "23:59"; // 確保有預設結束時間
    },

    getRouteTime: (state) => (date, index) => {
      return state.routeTimes[date]?.[index] ?? "00:00:00";
    },

    getStayDuration: (state) => (date, index) => {
      console.log(`🧐 getStayDuration(${date}, ${index}):`, {
        temp: state.tempStayDurations[date]?.[index],
        stay: state.stayDurations[date]?.[index],
      });
      return (
        state.tempStayDurations[date]?.[index] ??
        state.stayDurations[date]?.[index] ??
        0
      );
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

      const normalizedItinerary = itinerary
        .filter((place) => place !== null && place !== undefined)
        .map((place, index) => ({
          eventmappingId: place.eventmappingId ?? null,
          placeId: place.placeId ?? null,
          placeName: place.placeName ?? "",
          placeAddress: place.placeAddress ?? "",
          latitude: place.latitude ?? null,
          longitude: place.longitude ?? null,
          index: index,
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

    setEndTime(date, endTime) {
      this.endTimes[date] = endTime;
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
      console.log(
        `📌 存入 Pinia [${date}] index: ${index} =>`,
        this.stayDurations[date][index]
      );
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
      console.log(
        `📌 存暫時時間入 Pinia [${date}] index: ${index} =>`,
        this.stayDurations[date][index]
      );
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

    clearDayData(date) {
      if (!date) return;
      if (this.itineraryDates[date]) delete this.itineraryDates[date];
      if (this.startTimes[date]) delete this.startTimes[date];
      if (this.routeTimes[date]) delete this.routeTimes[date];
      if (this.stayDurations[date]) delete this.stayDurations[date];
      if (this.isEditingStays[date]) delete this.isEditingStays[date];
      if (this.tempStayDurations[date]) delete this.tempStayDurations[date];

      console.log(`🗑️ 已清除 ${date} 的行程暫存資料`);
    },

    /** 🔹 透過 scheduleStore 取得所有行程 */
    async loadAllItineraryData(tripId) {
      const scheduleStore = useScheduleStore();
      const placeStore = usePlaceStore();
      const allEvents = await scheduleStore.fetchAllEventsByTripId(tripId);

      const convertTimeToMinutes = (timeString) => {
        if (!timeString) return 0; // 預設為 0 分鐘

        if (
          typeof timeString === "string" &&
          timeString.match(/^\d{2}:\d{2}:\d{2}$/)
        ) {
          const [hours, minutes] = timeString.split(":").map(Number);
          return hours * 60 + minutes;
        }

        console.warn(`⚠️ 無法解析時間: ${timeString}`);
        return 0;
      };

      if (!allEvents || allEvents.length === 0) return;

      for (const event of allEvents) {
        const newDate = event.date;
        let placesWithDetails = [];

        if (event.eventXPlaceBeans) {
          console.log(`📅 處理日期：${newDate}`);

          const placeIds = event.eventXPlaceBeans.map((e) => e.placeId);
          await placeStore.fetchMultiplePlaces(placeIds);

          const sortedPlaces = event.eventXPlaceBeans.sort(
            (a, b) => a.placeOrder - b.placeOrder
          );

          sortedPlaces.forEach((eventPlace, index) => {
            const stayDurationMinutes = eventPlace.stayDuration
              ? convertTimeToMinutes(eventPlace.stayDuration)
              : 0;
            this.setStayDuration(newDate, index, stayDurationMinutes);
          });

          placesWithDetails = sortedPlaces.map((eventPlace, index) => {
            const placeDetails = placeStore.getPlaceDetailById(
              eventPlace.placeId
            );

            return {
              ...eventPlace,
              index,
              placeName: placeDetails?.placeName ?? "未知地點",
              placeAddress: placeDetails?.placeAddress ?? "未知地址",
              photos: placeDetails?.photos ?? [],
              latitude: placeDetails?.latitude ?? null,
              longitude: placeDetails?.longitude ?? null,
            };
          });
        }

        // **存入 Pinia**
        this.setItinerary(newDate, placesWithDetails);
        this.setStartTime(newDate, event.startTime ?? "08:00");
      }

      console.log("✅ 所有天數的行程已存入 Pinia：", this.itineraryDates);
    },

    clearAllData() {
      this.$reset();
      console.log("🗑️ 已重置所有行程數據");
    },
  },
});
