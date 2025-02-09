import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios"; // 全域匯入 axiosapi

export const useEventStore = defineStore("eventStore", {
  state: () => ({
    /**
     * 以日期做 key，存放當日的 event 與其 placeIds:
     * {
     *   "YYYY-MM-DD": {
     *     eventId: 101,
     *     date: "YYYY-MM-DD",
     *     placeIds: ["AAA", "BBB", ...],
     *     startTime: "08:00",
     *     endTime: null,
     *     notes: "...",
     *     ...
     *   },
     *   ...
     * }
     */
    eventsByDate: {},
    selectedEventId: null,
  }),

  // ✅ 新增 Getter
  getters: {
    /**
     * 由日期取得對應的 eventId；若沒找到則回傳 null
     * 用法： eventStore.getEventIdByDate("2025-01-01")
     */
    getEventIdByDate: (state) => (date) => {
      return state.eventsByDate[date]?.eventId ?? null;
    },
  },

  actions: {
    /**
     * 🔹 取得某天的 Event，並只存到 store 中必要資訊 (eventId, date, placeIds...)
     */
    async fetchEventByDate(tripId, date) {
      try {
        console.log(`🔍 [fetchEventByDate] GET /api/event/${tripId}/date/${date}`);
        const response = await axiosapi.get(`/api/event/${tripId}/date/${date}`);
        console.log("[fetchEventByDate] 伺服器回應:", response.data);

        if (!response.data || response.data.length === 0) {
          console.warn(`⚠️ 沒有找到 ${date} 的行程 (event)`);
          return null;
        }

        // 假設一天只有一個 event，若回傳多筆，你可以自行決定怎處理
        const event = response.data[0];

        // 從後端回傳的 places 提取出 placeIds
        const placeIds = (event.places || []).map((p) => p.placeId);

        // 在前端 store 只記錄必要欄位
        this.eventsByDate[date] = {
          eventId: event.eventId,
          date: event.date,
          placeIds,
          startTime: event.startTime || "08:00",
          endTime: event.endTime || null,
          notes: event.notes || "",
        };

        return this.eventsByDate[date];
      } catch (error) {
        console.error("❌ [fetchEventByDate] 無法取得行程:", error);
        return null;
      }
    },

    /**
     * 🔹 新增某天的 Event，成功後把 eventId 與日期等資訊存到 store
     */
    async addEvent(tripId, date) {
      try {
        const eventData = {
          schedule: { tripId },
          calendar: { date },
          startTime: "08:00",
          endTime: null,
          notes: "",
        };

        console.log("📡 [addEvent] POST /api/event", eventData);
        const response = await axiosapi.post(`/api/event`, eventData, {
          headers: { "Content-Type": "application/json" },
        });

        const newEvent = response.data;
        console.log("✅ [addEvent] 新增回應:", newEvent);

        // 在 store 中記錄此日の event
        this.eventsByDate[date] = {
          eventId: newEvent.eventId,
          date: newEvent.calendar.date,
          placeIds: [], // 初始無地點
          startTime: newEvent.startTime || "08:00",
          endTime: newEvent.endTime || null,
          notes: newEvent.notes || "",
        };

        return this.eventsByDate[date];
      } catch (error) {
        console.error("❌ [addEvent] 無法新增行程:", error?.response || error);
      }
    },

    /**
     * 🔹 更新某個 eventId (例如 startTime, endTime, notes...)，再同步到 store
     */
    async updateEvent(eventId, updateData) {
      try {
        console.log("📡 [updateEvent] PUT /api/event/update/", eventId, updateData);
        const response = await axiosapi.put(`/api/event/update/${eventId}`, updateData);
        const updatedEvent = response.data;
        console.log("✅ [updateEvent] 更新成功:", updatedEvent);

        // 從回傳資料中的日期找出 store 內的紀錄並更新
        const date = updatedEvent.calendar?.date;
        if (date && this.eventsByDate[date]) {
          this.eventsByDate[date] = {
            ...this.eventsByDate[date],
            startTime: updatedEvent.startTime,
            endTime: updatedEvent.endTime,
            notes: updatedEvent.notes,
          };
        }

        return this.eventsByDate[date] || null;
      } catch (error) {
        console.error("❌ [updateEvent] 更新行程內容失敗:", error);
      }
    },

    /**
     * 🔹 在某一天的 event 裡，新增一個地點 (placeId)
     */
    addPlaceToEvent(date, placeId) {
      if (!this.eventsByDate[date]) {
        console.warn(`⚠️ 尚未在 store 建立 ${date} 的 event。請先呼叫 fetchEventByDate 或 addEvent`);
        return;
      }
      if (!this.eventsByDate[date].placeIds.includes(placeId)) {
        this.eventsByDate[date].placeIds.push(placeId);
      }
    },

    /**
     * 🔹 從某一天的 event 中移除一個地點 (placeId)
     */
    removePlaceFromEvent(date, placeId) {
      if (!this.eventsByDate[date]) return;
      this.eventsByDate[date].placeIds = this.eventsByDate[date].placeIds.filter(
        (id) => id !== placeId
      );
    },
  },
});
