import { defineStore } from "pinia";
import { useItineraryStore } from "@/stores/ItineraryStore";
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
    async saveItineraryToBackend(eventId, selectedDate) {
      if (!eventId || !selectedDate) {
        console.warn("⚠️ eventId 或 selectedDate 無效，無法儲存");
        return;
      }

      const itineraryStore = useItineraryStore();

      const formattedStartTime = this.formatTime(
        itineraryStore.getStartTime(selectedDate)
      );
      const formattedEndTime = this.formatTime(
        itineraryStore.getEndTime(selectedDate)
      );

      // 準備要發送到後端的資料
      const formattedData = {
        eventId: eventId,
        startTime: formattedStartTime,
        endTime: formattedEndTime,
        notes: "",

        places: itineraryStore
          .getItineraryForDay(selectedDate)
          .map((place, index) => {
            const travelTimeRaw =
              itineraryStore.getRouteTime(selectedDate, index) ?? "00:00:00";
            const stayDurationRaw =
              itineraryStore.getStayDuration(selectedDate, index) ?? "00:00:00";

            console.log(`🕒 檢查 index ${index}:`, {
              eventmappingId: place.eventmappingId ?? null, // ✅ 確保包含 eventmappingId
              placeId: place.placeId,
              placeOrder: index + 1,
              travelTimeRaw,
              stayDurationRaw,
              formattedTravelTime: this.formatTime(travelTimeRaw),
              formattedStayDuration: this.formatTime(stayDurationRaw),
            });

            return {
              eventmappingId: place.eventmappingId ?? null, // ✅ 傳給後端，避免刪除舊關聯
              placeId: place.placeId,
              placeOrder: index + 1,
              travelTime: this.formatTime(travelTimeRaw), // ✅ 確保格式正確
              stayDuration: this.formatTime(stayDurationRaw),
              notes: place.notes || "",
            };
          }),
      };

      console.log(
        "🚀 發送到後端的資料:",
        JSON.stringify(formattedData, null, 2)
      );

      try {
        console.log("🚀 發送 API 更新行程...");
        const response = await axiosapi.put(
          `/api/eventXPlace/${eventId}`,
          formattedData,
          {
            headers: { "Content-Type": "application/json" },
          }
        );

        if (response.status !== 200) throw new Error("API 回應錯誤");

        console.log("✅ 行程已成功儲存", response.data);

        // 清除 Pinia 暫存資料
        itineraryStore.clearDayData(selectedDate);
      } catch (error) {
        console.error(
          "❌ 儲存行程時發生錯誤",
          error.response?.data || error.message
        );
        throw error; // 讓 `onBeforeRouteLeave` 決定如何處理錯誤
      }
    },

    //新增地點到行程（同步到後端）
    // async addPlaceToEvent(eventId, placeId) {
    //   try {
    //     console.log(
    //       `📡 [EventPlaceStore] POST /api/eventXPlace?eventId=${eventId}&placeId=${placeId}`
    //     );

    //     // 發送 API 請求（確保參數傳遞正確）
    //     const response = await axiosapi.post("/api/eventXPlace", null, {
    //       params: { eventId, placeId },
    //     });

    //     const newRelation = response.data;

    //     // ✅ 本地快取更新
    //     this.eventPlaceList.push(newRelation);

    //     console.log(
    //       `✅ [EventPlaceStore] 地點 ${placeId} 已加入行程 ${eventId}`
    //     );
    //     return newRelation;
    //   } catch (error) {
    //     console.error("❌ [EventPlaceStore] 加入地點失敗:", error);
    //     throw error;
    //   }
    // },

    // //移除地點
    // async removePlaceFromEvent(eventId, placeId) {
    //   try {
    //     console.log(
    //       `🗑️ [removePlaceFromEvent] DELETE /api/eventXPlace/${eventId}/${placeId}`
    //     );

    //     await axiosapi.delete(`/api/eventXPlace/${eventId}/${placeId}`);

    //     // ✅ 本地快取更新（過濾掉已刪除的關聯）
    //     this.eventPlaceList = this.eventPlaceList.filter(
    //       (relation) =>
    //         !(relation.eventId === eventId && relation.placeId === placeId)
    //     );

    //     console.log(
    //       `✅ [removePlaceFromEvent] 地點 ${placeId} 已從行程 ${eventId} 移除`
    //     );
    //   } catch (error) {
    //     console.error("❌ [removePlaceFromEvent] 無法移除地點:", error);
    //     throw error;
    //   }
    // },
  },
});
