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
import Swal from "sweetalert2"; // 確保引入 SweetAlert2

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
    const submitToBackend = async (details) => {
    if (!details) {
      console.error("無效的地點資料");
      return;
    }

    // 提取城市和地區
    const getCityAndRegion = (addressComponents) => {
      let city = "";
      let region = "";

      addressComponents.forEach((component) => {
          // 提取行政區域層級2 (台南市)
          if (component.types.includes("administrative_area_level_1")) {
            city = component.long_name; // 確保提取城市（例如：台南市）
          }
          // 提取行政區域層級3 (東區)
          if (component.types.includes("administrative_area_level_2")) {
            region = component.long_name; // 確保提取區域（例如：東區）
          }
        });
      return { city, region };
    };

    const { city, region } = getCityAndRegion(details.address_components || []);

    // 構建提交資料
    const formattedPlaceDetails = {
      placeType: details.types?.join(", ") || null,
      placeName: details.name || null,
      placeAddress: details.formatted_address || null,
      longitude: details.geometry?.location?.lng() || null,
      latitude: details.geometry?.location?.lat() || null,
      placeImage: details.photos?.[0]?.getUrl() || null,
      placePhone: details.formatted_phone_number || null,
      businessHours: details.opening_hours?.weekday_text?.join("\n") || null,
      rating: details.rating || null,
      placeInfo: details.user_ratings_total || null,
      website: details.website || null,
      bookingUrl: details.url || null,
      price: details.price_level || null,
      city: city || "未知城市",
      region: region || "未知地區",
      accommodationType: null, // 根據需求手動設定
      reservation: details.isReservable || null,
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
      Swal.fire({
        icon: "success",
        title: "成功",
        text: "地點已成功傳入後端!",
      });
      console.log("地點已成功傳入後端:", result);
    } catch (error) {
      console.error("傳送地點資訊至後端時出錯:", error);
      Swal.fire({
        icon: "error",
        title: "錯誤",
        text: `傳送地點資訊時出現錯誤: ${error.message}`,
      });
    }
  };

    // 初始化 Autocomplete
    const initAutocomplete = async () => {
      try {
        const { Autocomplete, PlacesService } = await google.maps.importLibrary("places");

        const autocomplete = new Autocomplete(document.getElementById("search-input"), {
          fields: ["place_id", "address_components", "geometry", "name"], // 最小化初始欄位
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
                "address_components",
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
.search input {
  width: 90%;
  padding: 12px;
  font-size: 18px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
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
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
