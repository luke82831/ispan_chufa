import { defineStore } from "pinia";
import axios from "axios";

// API 伺服器 URL
const API_BASE_URL = import.meta.env.VITE_API_URL;

export const useEventStore = defineStore("eventStore", {
  state: () => ({
    events: {}, // 存放每天的 event 資料 { "YYYY-MM-DD": eventData }
  }),

  actions: {
    /** 🔹 取得某天的 Event */

    async fetchEventByDate(tripId, date) {
      try {
        console.log(`🔍 發送 API 請求: /api/event/${tripId}/date/${date}`);

        const response = await axios.get(
          `${API_BASE_URL}/api/event/${tripId}/date/${date}`
        );
        console.log("📥 伺服器回應:", response.data);

        if (!response.data || response.data.length === 0) {
          console.warn(`⚠️ 沒有找到 ${date} 的行程內容`);
          return null;
        }

        const event = response.data[0]; // **取第一筆資料**
        console.log(
          `✅ 事件 ID: ${event.eventId}, startTime: ${event.startTime}`
        );

        return {
          ...event,
          startTime: event.startTime || "08:00:00", // ✅ 確保 `startTime` 不為 `undefined`
        };
      } catch (error) {
        console.error("❌ 無法取得行程內容:", error);
        return null;
      }
    },

    /** 🔹 新增 Event (如果當天沒有) */
    async addEvent(tripId, date) {
      try {
        const eventData = {
          tripId,
          date,
          startTime: "08:00", // 預設 08:00 出發
          endTime: null,
          notes: "",
        };

        console.log(
          "📡 發送 API 請求:",
          `${API_BASE_URL}/api/event/`,
          eventData
        );

        const response = await axios.post(
          `${API_BASE_URL}/api/event/`, // 確保這裡的 URL 正確
          eventData,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        console.log("✅ API 回應:", response.data);

        if (!this.events[date]) {
          this.events[date] = [];
        }
        this.events[date].push(response.data);

        return response.data;
      } catch (error) {
        console.error("❌ 無法新增行程內容:", error.response || error);
      }
    },

    /** 🔹 更新 Event */
    async updateEvent(eventId, updateData) {
      try {
        const response = await axios.put(
          `${API_BASE_URL}/api/event/update/${eventId}`,
          updateData
        );
        const updatedEvent = response.data;

        // 更新 store 內的 event
        if (this.events[updatedEvent.date]) {
          this.events[updatedEvent.date] = updatedEvent;
        }

        return updatedEvent;
      } catch (error) {
        console.error("❌ 更新行程內容失敗:", error);
      }
    },
  },
});
