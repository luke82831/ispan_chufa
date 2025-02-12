import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios"; // 全域 axiosapi

export const useEventPlaceStore = defineStore("eventPlaceStore", {
  state: () => ({
    eventPlaceList: [], // 存放所有 eventId ⇄ placeId 的對應關係
  }),

  actions: {
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
