import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios"; // 假設你有這個全域 axios

export const usePlaceStore = defineStore("placeStore", {
  state: () => ({
    placeDetailsMap: {},
    selectedPlaceId: null,
  }),

  getters: {
    getPlaceDetailById: (state) => (placeId) => {
      return state.placeDetailsMap[placeId] || null;
    },

    //取得當前選擇地點的資料
    selectedPlaceDetail(state) {
      if (!state.selectedPlaceId) return null;
      return state.placeDetailsMap[state.selectedPlaceId] || null;
    },
  },

  actions: {
    //根據 placeId 獲取完整地點資訊
    async fetchPlaceDetails(placeId) {
      if (!placeId) return null;

      // 🔹 先檢查快取，避免多次請求
      if (this.placeDetailsMap[placeId]) {
        return this.placeDetailsMap[placeId];
      }

      try {
        console.log(`📡 [PlaceStore] GET /api/place/${placeId}`);
        const response = await axiosapi.get(`/api/place/${placeId}`);

        if (response.data) {
          this.placeDetailsMap[placeId] = response.data; // ✅ 存入快取
          return response.data;
        } else {
          console.warn(`⚠️ 找不到 placeId ${placeId} 的詳細資訊`);
          return null;
        }
      } catch (error) {
        console.error(`❌ 無法取得 placeId ${placeId} 的詳細資訊:`, error);
        return null;
      }
    },

    //將已知的地點資料手動存進快取
    savePlaceToMap(place) {
      if (!place || !place.placeId) {
        console.warn(
          "⚠️ `place` 物件無效或缺少 `googlemapPlaceId`，無法儲存",
          place
        );
        return;
      }

      // ✅ 避免重複存入相同地點
      if (this.placeDetailsMap[place.placeId]) {
        console.log("⚠️ 該地點已存在 PlaceStore，跳過儲存:", place.placeId);
        return;
      }

      this.placeDetailsMap[place.placeId] = {
        ...place,
        latitude: place.latitude || null,
        longitude: place.longitude || null,
      };

      this.selectedPlaceId = place.placeId;
      console.log(
        "✅ 地點已存入 PlaceStore:",
        this.placeDetailsMap[place.placeId]
      );
    },

    //批次抓多個 placeId 的資訊
    async fetchMultiplePlaces(placeIds = []) {
      // 過濾已在快取的 placeIds
      const missingIds = placeIds.filter((id) => !this.placeDetailsMap[id]);
      if (missingIds.length === 0) {
        // 全部都已快取
        return placeIds.map((id) => this.placeDetailsMap[id]);
      }

      try {
        console.log(
          "📡 [fetchMultiplePlaces] POST /api/place/batch",
          missingIds
        );
        const response = await axiosapi.post(`/api/place/batch`, {
          placeIds: missingIds,
        });
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
