import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios"; // 假設你有這個全域 axios

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
     * 🔹 取得當前選擇地點的資料
     */
    selectedPlaceDetail(state) {
      if (!state.selectedPlaceId) return null;
      return state.placeDetailsMap[state.selectedPlaceId] || null;
    },
  },

  actions: {
    /**
     * 🔹 從後端取單筆地點資料並快取
     */
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
     * 🔹 將已知的地點資料手動存進快取
     */
    savePlaceToMap(place) {
      if (!place || !place.googlemapPlaceId) {
        console.warn("⚠️ `place` 物件無效或缺少 `googlemapPlaceId`，無法儲存", place);
        return;
      }

      // ✅ 避免重複存入相同地點
      if (this.placeDetailsMap[place.googlemapPlaceId]) {
        console.log("⚠️ 該地點已存在 PlaceStore，跳過儲存:", place.googlemapPlaceId);
        return;
      }

      this.placeDetailsMap[place.googlemapPlaceId] = {
        ...place,
        latitude: place.latitude || null,
        longitude: place.longitude || null,
      };

      this.selectedPlaceId = place.googlemapPlaceId;
      console.log("✅ 地點已存入 PlaceStore:", this.placeDetailsMap[place.googlemapPlaceId]);
    },

    /**
     * 🔹 批次抓多個 placeId 的資訊
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
        const places = response.data; // 後端回傳的陣列，每個元素是一個 placeDetail

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
