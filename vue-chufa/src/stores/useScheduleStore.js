import { defineStore } from "pinia";
import axios from "axios";

// 提取 API 伺服器的 URL
const API_BASE_URL = import.meta.env.VITE_API_URL;

export const useScheduleStore = defineStore("scheduleStore", {
  state: () => ({
    schedules: [], // 所有行程
    currentSchedule: null, // 當前選中的行程
  }),
  actions: {
    // 獲取「所有行程」列表
    async fetchSchedules() {
      try {
        console.log("開始請求 API...", API_BASE_URL); // 確認環境變數

        const response = await axios.get(`${API_BASE_URL}/api/schedules`);

        console.log("API 回傳資料:", response.data);
        this.schedules = response.data;
      } catch (error) {
        console.error("載入行程失敗:", error);
      }
    },

    // 透過 tripId 獲取「單一行程」及其 `events`
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

    // 刪除行程
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
