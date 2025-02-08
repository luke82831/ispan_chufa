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
    /** 🔹 設定當前選擇的schedule日期 */
    setSelectedDate(date) {
      this.selectedDate = date;
    },

    /** 🔹 獲取所有schedule */
    async fetchSchedules() {
      try {
        console.log("開始請求 API...", API_BASE_URL);

        // 從 localStorage 取得 JWT Token
        const token = localStorage.getItem("token");

        // 確保 token 存在
        if (!token) {
          console.error("沒有 JWT Token，請先登入");
          return;
        }

        // 透過 axios 傳送請求，帶上 Authorization 標頭
        const response = await axios.get(`${API_BASE_URL}/api/schedules`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        console.log("API 回傳資料:", response.data);
        this.schedules = response.data;
      } catch (error) {
        console.error("載入行程失敗:", error);
      }
    },

    /** 🔹 獲取特定schedule */
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

    /** 🔹 刪除schedule */
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

    /** 🔹 修改schedule */
    async updateScheduleEndDate(tripId, newEndDate) {
      try {
        await axios.put(`${API_BASE_URL}/api/schedule/${tripId}`, {
          endDate: newEndDate,
        });

        // 更新本地 store
        if (this.currentSchedule) {
          this.currentSchedule.endDate = newEndDate;
        }
      } catch (error) {
        console.error("更新行程結束日期失敗:", error);
        throw error;
      }
    },
  },
});
