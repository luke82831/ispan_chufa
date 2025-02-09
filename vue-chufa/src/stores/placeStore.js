import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios"; // 全域匯入 axiosapi

export const usePlaceStore = defineStore("placeStore", {
  state: () => ({
    /**
     * 快取 placeId -> placeDetail
     * 例如：
     * {
     *   "AAA": { placeId: "AAA", name: "地點A", ... },
     *   "BBB": { placeId: "BBB", name: "地點B", ... }
     * }
     */
    placeDetailsMap: {},
    selectedPlaceId: null,
  }),

  getters: {
    /**
     * 🔹 若只想透過 placeId 拿到地點資料，可以做個 getter。
     *    用法：placeStore.getPlaceDetailById("AAA")
     */
    getPlaceDetailById: (state) => (placeId) => {
      return state.placeDetailsMap[placeId] || null;
    },

    /**
     * 🔹 也可以加一個「取得當前選擇地點的資料」的 getter
     */
    selectedPlaceDetail(state) {
      if (!state.selectedPlaceId) return null;
      return state.placeDetailsMap[state.selectedPlaceId] || null;
    },
  },

  actions: {
    async fetchPlaceDetail(placeId) {
      // 如果快取有資料，就直接回傳
      if (this.placeDetailsMap[placeId]) {
        return this.placeDetailsMap[placeId];
      }

      // 若無資料，呼叫 API
      try {
        console.log("🔍 [fetchPlaceDetail] GET /api/place/", placeId);
        const response = await axiosapi.get(`/api/place/${placeId}`);
        const detail = response.data;
        console.log("📥 [fetchPlaceDetail] 伺服器回應:", detail);

        // 放進快取
        this.placeDetailsMap[placeId] = detail;
        return detail;
      } catch (error) {
        console.error("❌ [fetchPlaceDetail] 無法取得地點詳細資訊:", error);
        return null;
      }
    },

    /**
     * 🔹 如果有需要一次性抓多個 placeId，你可以做一個批次取得函式
     */
    async fetchMultiplePlaces(placeIds = []) {
      // 過濾已在快取的 placeIds
      const missingIds = placeIds.filter((id) => !this.placeDetailsMap[id]);
      if (missingIds.length === 0) {
        // 全部都已快取
        return placeIds.map((id) => this.placeDetailsMap[id]);
      }

      try {
        console.log("📡 [fetchMultiplePlaces] POST /api/places/batch", missingIds);
        const response = await axiosapi.post(`/api/places/batch`, { placeIds: missingIds });
        const places = response.data; // 期望後端回傳陣列，每個元素是一個 placeDetail

        // 放入快取
        places.forEach((pl) => {
          this.placeDetailsMap[pl.placeId] = pl;
        });

        // 最後回傳所有 placeIds 的完整資料
        return placeIds.map((id) => this.placeDetailsMap[id]);
      } catch (error) {
        console.error("❌ [fetchMultiplePlaces] 無法批次取得地點:", error);
        return [];
      }
    },
  },
});
