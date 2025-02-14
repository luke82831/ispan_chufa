import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios"; // 假設你有這個全域 axios

export const useScheduleStore = defineStore("scheduleStore", {
  state: () => ({
    schedules: [], // 所有行程列表
    currentSchedule: null, // 當前選中的行程
    selectedDate: "", // 當前選中的行程日期
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
        console.log("開始請求 API...");
        const response = await axiosapi.get("/api/schedules");
        console.log("API 回傳資料:", response.data);
        this.schedules = response.data;
      } catch (error) {
        console.error("載入行程失敗:", error);
      }
    },

    /** 🔹 獲取特定schedule */
    async fetchScheduleById(tripId) {
      try {
        const response = await axiosapi.get(`/api/schedule/${tripId}`);
        this.currentSchedule = response.data;
      } catch (error) {
        console.error("載入行程詳細資料失敗:", error);
      }
    },

    /** 🔹 刪除schedule */
    async deleteSchedule(tripId) {
      try {
        await axiosapi.delete(`/api/schedule/${tripId}`);
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
        await axiosapi.put(`/api/schedule/${tripId}`, {
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

    async updateScheduleTitle(tripId, newTitle) {
      try {
        await axiosapi.patch(`/api/schedule/${tripId}`, {
          tripName: newTitle,
        });

        // 更新本地 store
        if (this.currentSchedule && this.currentSchedule.tripId === tripId) {
          this.currentSchedule.tripName = newTitle;
        }

        // 更新 schedules 陣列中的對應行程
        const schedule = this.schedules.find(
          (schedule) => schedule.tripId === tripId
        );
        if (schedule) {
          schedule.tripName = newTitle;
        }
      } catch (error) {
        console.error("更新行程標題失敗:", error);
        throw error;
      }
    },
  },
});
