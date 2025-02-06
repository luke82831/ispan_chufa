import { defineStore } from "pinia";
import { format } from "date-fns";
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
        // ✅ 確保 `date` 為 `YYYY-MM-DD`
        const formattedDate =
          typeof date === "string" && date.includes("-")
            ? date // **如果已經是 `YYYY-MM-DD`，則不轉換**
            : format(new Date(date), "yyyy-MM-dd"); // **否則轉換為 `YYYY-MM-DD`**

        console.log(
          `🔍 發送 API 請求: /api/event/${tripId}/date/${formattedDate}`
        );

        const response = await axios.get(
          `${API_BASE_URL}/api/event/${tripId}/date/${formattedDate}`
        );

        console.log("📥 伺服器回應:", response.data);

        if (!response.data || response.data.length === 0) {
          console.warn(`⚠️ 沒有找到 ${formattedDate} 的行程內容`);
          this.events[formattedDate] = null;
          return null;
        }

        response.data.forEach((event) => {
          console.log(
            `✅ 事件 ID: ${event.eventId}, 日期: ${event.calendar.date}`
          );
        });

        this.events[formattedDate] = response.data;
        return response.data;
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

        const response = await axios.post(
          `${API_BASE_URL}/api/event/`,
          eventData
        );
        this.events[date] = response.data;
        return response.data;
      } catch (error) {
        console.error("❌ 無法新增行程內容:", error);
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
