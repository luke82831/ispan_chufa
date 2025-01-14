<template>
  <div>
    <input
      id="search-input"
      type="text"
      v-model="searchInput"
      placeholder="請輸入地點"
    />
    <!-- 新增儲存地點的按鈕 -->
    <button @click="submitToBackend(placeDetails)" :disabled="!placeDetails">儲存地點</button>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  props: {
    onPlaceSelected: {
      type: Function,
      required: true,
    },
  },
  setup(props) {
    const searchInput = ref(""); // 搜尋框輸入
    const placeDetails = ref(null); // 儲存地點詳細資訊

    // 提交地點詳細資訊到後端
    const submitToBackend = async (placeDetails) => {
      if (!placeDetails) {
        console.error("無效的地點資料");
        return;
      }

      const formattedPlaceDetails = {
        placeType: placeDetails.types?.join(", ") || null,
        placeName: placeDetails.name || null,
        placeAddress: placeDetails.formatted_address || null,
        longitude: placeDetails.geometry?.location?.lng() || null,
        latitude: placeDetails.geometry?.location?.lat() || null,
        placeImage: placeDetails.photos?.[0]?.getUrl() || null,
        placePhone: placeDetails.formatted_phone_number || null,
        businessHours: placeDetails.opening_hours?.weekday_text?.join("\n") || null,
        rating: placeDetails.rating || null,
        placeInfo: placeDetails.user_ratings_total || null,
        website: placeDetails.website || null,
        bookingUrl: placeDetails.url || null,
        price: placeDetails.price_level || null,
        accommodationType: null, // 根據需求手動設定
        mealTime: null,          // 根據需求手動設定
        reservation: placeDetails.isReservable || null,
      };

      try {
        const response = await fetch("http://localhost:8080/api/savePlace", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formattedPlaceDetails),
        });

        if (!response.ok) {
          throw new Error(`伺服器錯誤: ${response.statusText}`);
        }

        const result = await response.json();
        console.log("地點已成功傳入後端:", result);
      } catch (error) {
        console.error("傳送地點資訊至後端時出錯:", error);
      }
    };

    // 初始化 Autocomplete
    const initAutocomplete = async () => {
      try {
        const { Autocomplete, PlacesService } = await google.maps.importLibrary("places");

        const autocomplete = new Autocomplete(document.getElementById("search-input"), {
          fields: ["place_id"], // 最小化初始欄位
          types: ['establishment'] // 只顯示地點名稱
        });

        // 限制地點搜尋在台灣
        autocomplete.setOptions({
          componentRestrictions: { country: "TW" },
        });

        autocomplete.addListener("place_changed", async () => {
          const place = autocomplete.getPlace();

          // 確保地點有效且包含 place_id
          if (place && place.place_id) {
            console.log("Place ID:", place.place_id);

            // 使用 Place.fetchFields() 取得地點詳細資訊
            const service = new PlacesService(document.createElement("div")); // 建立 PlacesService 實例
            const request = {
              placeId: place.place_id,
              fields: [
                "place_id",
                "name",
                "formatted_address",
                "geometry",
                "rating",
                "opening_hours",
                "photos",
                "types",
                "formatted_phone_number",
                "user_ratings_total",
                "website",
                "price_level",
                "url",
              ],
            };

            service.getDetails(request, (details, status) => {
              if (status === google.maps.places.PlacesServiceStatus.OK && details) {
                console.log("地點詳細資訊:", details);
                placeDetails.value = details; // 更新地點詳細資訊
                searchInput.value = details.name; // 更新搜尋欄為選擇的地點名稱
                props.onPlaceSelected(details); // 傳遞給父組件
              } else {
                console.error("取得地點詳細資訊失敗:", status);
              }
            });
          }
        });
      } catch (error) {
        console.error("Autocomplete 初始化失敗:", error);
      }
    };

    return {
      searchInput,
      placeDetails,
      initAutocomplete,
      submitToBackend, // 提交方法
    };
  },

  mounted() {
    this.initAutocomplete();
  },
};
</script>

<style scoped>
.search-section input {
  width: 80%;
  padding: 12px;
  font-size: 18px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  background-color: #4CAF50;
  color: white;
  margin-top: 10px;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
