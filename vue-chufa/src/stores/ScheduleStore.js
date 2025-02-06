import { defineStore } from "pinia";
import axios from "axios";

// 提取 API 伺服器的 URL
const API_BASE_URL = import.meta.env.VITE_API_URL;

export const useScheduleStore = defineStore("scheduleStore", {
  state: () => ({
    schedules: [], // 所有行程列表
    currentSchedule: null, // 當前選中的行程
    selectedDate: null, // 當前選中的行程日期
    itinerary: {}, // 行程地點資料 { "YYYY-MM-DD": [地點列表] }
  }),

  actions: {
    /** 🔹 獲取所有行程 */
    async fetchSchedules() {
      try {
        console.log("開始請求 API...", API_BASE_URL);
        const response = await axios.get(`${API_BASE_URL}/api/schedules`);
        console.log("API 回傳資料:", response.data);
        this.schedules = response.data;
      } catch (error) {
        console.error("載入行程失敗:", error);
      }
    },

    /** 🔹 獲取特定行程 */
    async fetchScheduleById(tripId) {
      try {
        const response = await axios.get(
          `${API_BASE_URL}/api/schedule/${tripId}`
        );
        this.currentSchedule = response.data;
      } catch (error) {
        console.error("載入行程詳細資料失敗:", error);
      }
    },

    /** 🔹 設定當前選擇的行程日期 */
    setSelectedDate(date) {
      this.selectedDate = date;
    },

    /** 🔹 加入地點到行程 */
    async addPlaceToSchedule(date, placeDetails) {
      if (!this.currentSchedule?.tripId) {
        console.error("❌ currentSchedule.tripId 未設定，無法加入行程");
        return;
      }

      const itineraryData = {
        tripId: this.currentSchedule.tripId,
        date: date,
        placeId: placeDetails.id,
        placeName: placeDetails.displayName,
        latitude: placeDetails.location.lat,
        longitude: placeDetails.location.lng,
        stayDuration: 60, // 預設停留 60 分鐘
        placeOrder: this.itinerary[date]?.length || 0, // 設定地點順序
      };

      try {
        const response = await axios.post(
          `${API_BASE_URL}/api/itinerary/add`,
          itineraryData
        );
        const newPlace = response.data;
        console.log("✅ 加入行程成功:", newPlace);

        // **更新 Pinia store**
        if (!this.itinerary[date]) this.itinerary[date] = [];
        this.itinerary[date].push(newPlace);
      } catch (error) {
        console.error("❌ 無法加入行程:", error);
      }
    },

    /** 🔹 刪除行程 */
    async deleteSchedule(tripId) {
      try {
        await axios.delete(`${API_BASE_URL}/api/schedule/${tripId}`);
        this.schedules = this.schedules.filter(
          (schedule) => schedule.tripId !== tripId
        );
        console.log(`行程 ${tripId} 已刪除`);
      } catch (error) {
        console.error("刪除行程失敗:", error);
      }
    },
  },
});
