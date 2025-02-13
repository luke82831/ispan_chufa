import { defineStore } from "pinia";
import { useEventPlaceStore } from "@/stores/EventPlaceStore";
import axiosapi from "@/plugins/axios"; // 全域匯入 axiosapi

export const useEventStore = defineStore("eventStore", {
  state: () => ({
    eventsByDate: {}, // 以日期為 key，存放當日的 event
    selectedEventId: null,
  }),

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
    //從 eventXPlaceBeans 提取 placeIds
    extractPlaceIds(event) {
      return (event.eventXPlaceBeans || []).map((p) => p.placeId);
    },

    //取得某天的 Event
    async fetchEventByDate(tripId, date) {
      try {
        // 確保 date 是 string，並轉換為 YYYY-MM-DD 格式
        const formattedDate = new Date(date).toISOString().split("T")[0];

        console.log(`📡 查詢 event: tripId=${tripId}, date=${formattedDate}`);

        const response = await axiosapi.get(
          `/api/event/${tripId}/date/${formattedDate}`
        );

        if (!response.data || response.data.length === 0) {
          console.warn(`⚠️ 沒有找到 ${formattedDate} 的行程 (event)`);
          return null;
        }

        const event = response.data[0];
        const placeIds = this.extractPlaceIds(event);
        const eventXPlaceBeans = event.eventXPlaceBeans || [];

        this.eventsByDate[formattedDate] = {
          eventId: event.eventId,
          date: event.date,
          eventXPlaceBeans,
          placeIds,
          startTime: event.startTime || "08:00",
          endTime: event.endTime || null,
          notes: event.notes || "",
        };

        return this.eventsByDate[formattedDate];
      } catch (error) {
        console.error("❌ [fetchEventByDate] 無法取得行程:", error);
        return null;
      }
    },

    //新增某天的 Event，成功後存到 store
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

        this.eventsByDate[date] = {
          eventId: newEvent.eventId,
          date: newEvent.calendar.date,
          eventXPlaceBeans: [],
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

    //讓 event 內部新增地點，但真正的後端請求交給 EventPlaceStore
    async addPlaceToEvent(eventId, placeId) {
      const eventPlaceStore = useEventPlaceStore();
      try {
        await eventPlaceStore.addPlaceToEvent(eventId, placeId);

        const event = Object.values(this.eventsByDate).find(
          (e) => e.eventId === eventId
        );
        if (event && !event.placeIds.includes(placeId)) {
          event.placeIds.push(placeId);
        }

        console.log(
          `✅ [EventStore] 地點 ${placeId} 已成功加入 Event ${eventId}`
        );
      } catch (error) {
        console.error("❌ [EventStore] 無法加入地點:", error);
      }
    },
  },
});
