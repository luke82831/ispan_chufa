import { defineStore } from "pinia";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { useEventStore } from "@/stores/EventStore";
import axiosapi from "@/plugins/axios"; // 全域 axiosapi

export const useEventPlaceStore = defineStore("eventPlaceStore", {
  state: () => ({
    eventPlaceList: [], // 存放所有 eventId ⇄ placeId 的對應關係
  }),

  actions: {
    //取得某個 eventId 內的所有 placeId
    async fetchPlacesByEvent(eventId) {
      try {
        console.log(`🔍 [fetchPlacesByEvent] GET /api/eventXPlace/${eventId}`);

        const response = await axiosapi.get(`/api/eventXPlace/${eventId}`);
        const places = response.data; // 直接獲取 placeId 陣列

        console.log(
          `✅ [fetchPlacesByEvent] 已獲取 eventId ${eventId} 的 placeIds:`,
          places
        );
        return places;
      } catch (error) {
        console.error(
          `❌ [fetchPlacesByEvent] 無法獲取 event ${eventId} 的地點:`,
          error
        );
        return [];
      }
    },

    formatTime(time) {
      if (!time) return "00:00:00";

      if (typeof time === "number") {
        // ✅ 如果是數字（分鐘數），轉換成 `HH:mm:ss`
        const hours = Math.floor(time / 60);
        const minutes = time % 60;
        return `${String(hours).padStart(2, "0")}:${String(minutes).padStart(
          2,
          "0"
        )}:00`;
      }

      if (typeof time === "string") {
        const parts = time.split(":").map(Number);
        if (parts.some(isNaN) || parts.length < 2) return "00:00:00"; // 防止錯誤格式
        return `${String(parts[0]).padStart(2, "0")}:${String(
          parts[1]
        ).padStart(2, "0")}:00`;
      }

      if (time instanceof Date) {
        const hours = String(time.getHours()).padStart(2, "0");
        const minutes = String(time.getMinutes()).padStart(2, "0");
        return `${hours}:${minutes}:00`;
      }

      return "00:00:00"; // 預設值
    },

    //將行程資料傳遞到後端
    // async saveItineraryToBackend(eventId, selectedDate) {
    //   if (!eventId || !selectedDate) {
    //     console.warn("⚠️ eventId 或 selectedDate 無效，無法儲存");
    //     return;
    //   }

    //   const itineraryStore = useItineraryStore();

    //   const formattedStartTime = this.formatTime(
    //     itineraryStore.getStartTime(selectedDate)
    //   );
    //   const formattedEndTime = this.formatTime(
    //     itineraryStore.getEndTime(selectedDate)
    //   );

    //   // 準備要發送到後端的資料
    //   const formattedData = {
    //     eventId: eventId,
    //     startTime: formattedStartTime,
    //     endTime: formattedEndTime,
    //     notes: "",

    //     places: itineraryStore
    //       .getItineraryForDay(selectedDate)
    //       .map((place, index) => {
    //         const travelTimeRaw =
    //           itineraryStore.getRouteTime(selectedDate, index) ?? "00:00:00";
    //         const stayDurationRaw =
    //           itineraryStore.getStayDuration(selectedDate, index) ?? "00:00:00";

    //         console.log(`🕒 檢查 index ${index}:`, {
    //           eventmappingId: place.eventmappingId ?? null,
    //           placeId: place.placeId,
    //           placeOrder: index + 1,
    //           travelTimeRaw,
    //           stayDurationRaw,
    //           formattedTravelTime: this.formatTime(travelTimeRaw),
    //           formattedStayDuration: this.formatTime(stayDurationRaw),
    //         });

    //         return {
    //           eventmappingId: place.eventmappingId ?? null,
    //           placeId: place.placeId,
    //           placeOrder: index + 1,
    //           travelTime: this.formatTime(travelTimeRaw), // ✅ 確保格式正確
    //           stayDuration: this.formatTime(stayDurationRaw),
    //           notes: place.notes || "",
    //         };
    //       }),
    //   };

    //   console.log(
    //     "🚀 發送到後端的資料:",
    //     JSON.stringify(formattedData, null, 2)
    //   );

    //   try {
    //     console.log("🚀 發送 API 更新行程...");
    //     const response = await axiosapi.put(
    //       `/api/eventXPlace/${eventId}`,
    //       formattedData,
    //       {
    //         headers: { "Content-Type": "application/json" },
    //       }
    //     );

    //     if (response.status !== 200) throw new Error("API 回應錯誤");

    //     console.log("✅ 行程已成功儲存", response.data);

    //     // 清除 Pinia 暫存資料
    //     itineraryStore.clearDayData(selectedDate);
    //   } catch (error) {
    //     console.error(
    //       "❌ 儲存行程時發生錯誤",
    //       error.response?.data || error.message
    //     );
    //     throw error; // 讓 `onBeforeRouteLeave` 決定如何處理錯誤
    //   }
    // },
    async saveItineraryToBackend(tripId, selectedDates) {
      if (!tripId || !selectedDates || selectedDates.length === 0) {
        console.warn("⚠️ tripId 或 selectedDates 無效，無法儲存");
        return;
      }

      const itineraryStore = useItineraryStore();
      const eventStore = useEventStore(); // 🔥 確保使用 eventStore

      // 🔹 確保 `selectedDates` 是陣列
      const dates = Array.isArray(selectedDates)
        ? selectedDates
        : [selectedDates];

      // 🔹 生成所有日期的行程數據
      const formattedData = dates
        .map((date) => {
          const eventId = eventStore.getEventIdByDate(date); // ✅ 正確取得 `eventId`

          if (!eventId) {
            console.warn(`⚠️ 找不到 ${date} 對應的 eventId，跳過該天儲存`);
            return null;
          }

          console.log(`📅 確認對應: 日期=${date}, 取得的 eventId=${eventId}`);

          return {
            eventId: eventId, // 🔥 這裡確保 `eventId` 來自 eventStore
            date: date,
            startTime: this.formatTime(itineraryStore.getStartTime(date)),
            endTime: this.formatTime(itineraryStore.getEndTime(date)),
            notes: "",

            places: itineraryStore
              .getItineraryForDay(date)
              .map((place, index) => ({
                eventmappingId: place.eventmappingId ?? null,
                placeId: place.placeId,
                placeOrder: index + 1,
                travelTime: this.formatTime(
                  itineraryStore.getRouteTime(date, index) ?? "00:00:00"
                ),
                stayDuration: this.formatTime(
                  itineraryStore.getStayDuration(date, index) ?? "00:00:00"
                ),
                notes: place.notes || "",
              })),
          };
        })
        .filter((data) => data !== null); // 🔥 過濾掉無效的資料

      console.log(
        "🚀 發送到後端的資料:",
        JSON.stringify(formattedData, null, 2)
      );

      try {
        console.log("🚀 發送 API 更新行程...");
        const response = await axiosapi.put(
          `/api/eventXPlace/${tripId}/batch`,
          formattedData,
          { headers: { "Content-Type": "application/json" } }
        );

        if (response.status !== 200) throw new Error("API 回應錯誤");

        console.log("✅ 所有行程已成功儲存", response.data);

        // 🔹 清除所有天數的 Pinia 暫存資料
        dates.forEach((date) => itineraryStore.clearDayData(date));
      } catch (error) {
        console.error(
          "❌ 儲存行程時發生錯誤",
          error.response?.data || error.message
        );
        throw error;
      }
    },

    //新增地點到行程（同步到後端）
    async addPlaceToEvent(eventId, placeId) {
      try {
        console.log(
          `📡 [EventPlaceStore] POST /api/eventXPlace?eventId=${eventId}&placeId=${placeId}`
        );

        // 發送 API 請求（確保參數傳遞正確）
        const response = await axiosapi.post("/api/eventXPlace", null, {
          params: { eventId, placeId },
        });

        const newRelation = response.data;

        // ✅ 本地快取更新
        this.eventPlaceList.push(newRelation);

        console.log(
          `✅ [EventPlaceStore] 地點 ${placeId} 已加入行程 ${eventId}`
        );
        return newRelation;
      } catch (error) {
        console.error("❌ [EventPlaceStore] 加入地點失敗:", error);
        throw error;
      }
    },

    //移除地點
    async removePlaceFromEvent(eventId, placeId) {
      try {
        console.log(
          `🗑️ [removePlaceFromEvent] DELETE /api/eventXPlace/${eventId}/${placeId}`
        );

        await axiosapi.delete(`/api/eventXPlace/${eventId}/${placeId}`);

        // ✅ 本地快取更新（過濾掉已刪除的關聯）
        this.eventPlaceList = this.eventPlaceList.filter(
          (relation) =>
            !(relation.eventId === eventId && relation.placeId === placeId)
        );

        console.log(
          `✅ [removePlaceFromEvent] 地點 ${placeId} 已從行程 ${eventId} 移除`
        );
      } catch (error) {
        console.error("❌ [removePlaceFromEvent] 無法移除地點:", error);
        throw error;
      }
    },
  },
});
