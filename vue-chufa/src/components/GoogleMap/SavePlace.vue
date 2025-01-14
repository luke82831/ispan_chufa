<template>
  <div>
    <button @click="saveSelectedPlace" :disabled="isSaving">
      <span v-if="isSaving">儲存中...</span>
      <span v-else>儲存地點</span>
    </button>
    <p v-if="selectedPlace && selectedPlace.displayName">
      已選擇地點：{{ selectedPlace.displayName }} - {{ selectedPlace.formattedAddress }}
    </p>
  </div>
</template>

<script>
import { ref, toRaw } from "vue"; // 引入 toRaw()
import axios from "axios";
import Swal from "sweetalert2";

axios.defaults.baseURL = "http://localhost:8080"; // 後端基礎路徑

export default {
  props: {
    selectedPlace: {
      type: Object,
      required: false, // 設定為非必填
      default: () => null, // 預設為 null
    },
  },
  setup(props) {
    const isSaving = ref(false);

    // 定義 fetchFields() 函式
    const fetchFields = (placeId) => {
      return {
        placeId: placeId,
        fields: [
          "name",
          "formatted_address",
          "opening_hours",
          "photos",
          "rating",
          "geometry",
        ],
      };
    };

    // 獲取 Google Place 詳細資料的函式
    const getPlaceDetails = async (placeId) => {
      try {
        const stringPlaceId = String(placeId);
        const { places } = await google.maps.importLibrary("places");

        const service = new places.PlacesService(document.createElement("div"));
        const request = {
          placeId: stringPlaceId,
          fields: [
            "name",
            "formatted_address",
            "opening_hours",
            "photos",
            "rating",
            "geometry",
          ],
        };

        return new Promise((resolve, reject) => {
          service.getDetails(request, (place, status) => {
            if (status === google.maps.places.PlacesServiceStatus.OK && place) {
              const placeData = {
                displayName: place.name,
                formattedAddress: place.formatted_address,
                regularOpeningHours: place.opening_hours
                  ? place.opening_hours.weekday_text
                  : "無營業時間資訊",
                rating: place.rating || null,
                photos: place.photos
                  ? place.photos.map((photo) => photo.getUrl())
                  : [],
                latitude: place.geometry.location.lat(),
                longitude: place.geometry.location.lng(),
              };
              resolve(placeData);
            } else {
              console.error("Google Places API Error:", status);
              reject(new Error(`無法獲取地點詳細資料 (狀態碼: ${status})`));
            }
          });
        });
      } catch (error) {
        throw new Error(`無法獲取詳細資料: ${error.message}`);
      }
    };

    // 儲存地點的函式
    const saveSelectedPlace = async () => {
      const rawSelectedPlace = toRaw(props.selectedPlace);

      if (!rawSelectedPlace || !rawSelectedPlace.place_id) {
        Swal.fire({
          icon: "error",
          title: "無效地點",
          text: "請選擇有效的地點後再進行儲存。",
        });
        return;
      }

      const placeId = rawSelectedPlace.place_id;

      try {
        const detailedPlace = await getPlaceDetails(placeId);
        isSaving.value = true;

        // 使用 fetchFields 函式來獲取需要的資料格式
        const fetchFieldsRequest = fetchFields(placeId);

        const response = await axios.post("/api/savePlace", fetchFieldsRequest, {
          headers: { "Content-Type": "application/json" },
        });

        console.log("地點儲存成功:", response.data);
        Swal.fire({
          icon: "success",
          title: "儲存成功！",
          text: "地點已成功儲存。",
        });
      } catch (error) {
        console.error("地點儲存失敗:", error);
        Swal.fire({
          icon: "error",
          title: "儲存失敗",
          text: `儲存過程中發生錯誤：${error.message}`,
        });
      } finally {
        isSaving.value = false;
      }
    };

    return {
      isSaving,
      saveSelectedPlace,
    };
  },
};
</script>

<style scoped>
button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
