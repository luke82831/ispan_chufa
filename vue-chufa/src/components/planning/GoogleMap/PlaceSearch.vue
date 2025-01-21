<template>
  <div>
    <input id="search-input" type="text" v-model="searchInput" placeholder="請輸入地點" />
    <!-- 新增儲存地點的按鈕 -->
    <button @click="submitToBackend(placeDetails)" :disabled="!placeDetails">
      儲存地點
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from "vue";
import Swal from "sweetalert2"; // 確保引入 SweetAlert2

const searchInput = ref(""); // 搜尋框輸入
const placeDetails = ref(null); // 儲存地點詳細資訊
const emit = defineEmits(["place-selected"]); // 定義 place-selected 事件

// 初始化 Autocomplete
const initAutocomplete = async () => {
  try {
    // const { Autocomplete, PlacesService } = await google.maps.importLibrary("places");
    const { Autocomplete } = await google.maps.importLibrary("places");

    const autocomplete = new Autocomplete(document.getElementById("search-input"), {
      fields: ["place_id", "address_components", "geometry", "name"], // 最小化初始欄位
      types: ["establishment"], // 只顯示地點名稱
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

        //檢查後端是否已有資料
        const placeExists = await checkPlaceInBackend(place.place_id);

        if (placeExists) {
          // 資料已存在，直接使用後端資料更新
          placeDetails.value = placeExists; // 儲存後端回傳的地點詳細資料
          searchInput.value = placeExists.placeName; // 更新搜尋欄
          emit("place-selected", placeExists); // 傳遞資料給父組件
          console.log("傳遞給父組件");
        } else {
          // 資料不存在，從 Google Places API 獲取詳細資訊並儲存到後端
          fetchPlaceDetailsFromGoogle(place);
        }
      }
    });
  } catch (error) {
    console.error("Autocomplete 初始化失敗:", error);
  }
};

// 從後端查詢地點資料
const checkPlaceInBackend = async (placeId) => {
  try {
    const response = await fetch("http://localhost:8080/api/checkPlace", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ placeId }),
    });

    if (response.ok) {
      const result = await response.json();
      if (result.message === "地點已存在資料庫") {
        console.log("後端地點資料:", result.placeInfo);
        return result.placeInfo; // 返回後端儲存的地點資料
      }
    }
    return null; // 資料不存在
  } catch (error) {
    console.error("查詢後端地點資料時出錯:", error);
    return null;
  }
};

//  從 Google Places API 獲取詳細資訊並提交到後端
const fetchPlaceDetailsFromGoogle = (place) => {
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

      // 提交資料到後端
      submitToBackend(details);
      placeDetails.value = details; // 更新地點詳細資訊
      searchInput.value = details.name; // 更新搜尋欄為選擇的地點名稱
      //emit("place-selected", details); // 使用 emit 傳遞地點資料給父組件
    } else {
      console.error("取得地點詳細資訊失敗:", status);
    }
  });
};

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

  // 提取並格式化資料
  const formattedPlaceDetails = {
    googlemapPlaceId: details.place_id || null,
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
    priceLevel: details.price_level || null,
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

    if (response.ok) {
      const result = await response.json();
      Swal.fire({
        icon: "success",
        title: "成功",
        text: "地點已成功儲存到後端!",
      });
      console.log("地點已儲存:", result);
    } else {
      throw new Error(`伺服器錯誤: ${response.statusText}`);
    }
  } catch (error) {
    console.error("儲存地點資訊時出錯:", error);
    Swal.fire({
      icon: "error",
      title: "錯誤",
      text: `儲存地點資訊時出現錯誤: ${error.message}`,
    });
  }
};

// 初始化 Autocomplete 並設置監聽
onMounted(() => {
  initAutocomplete();
});
/*
  // 假設 formattedPlaceDetails 包含 placeAddress（唯一識別地點的地址）
  const placeAddress = formattedPlaceDetails.placeAddress;
  // console.log("取出地址: " + placeAddress); // 確認取出的地址

  try {
    // 用 POST 方法查詢後端是否已有該地址的資料
    const checkResponse = await fetch("http://localhost:8080/api/checkPlaceByAddress", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ address: placeAddress }),
    });

    if (!checkResponse.ok) {
      console.log("checkResult:", checkResult);
      throw new Error(`伺服器錯誤: ${checkResponse.statusText}`);
    }

    const checkResult = await checkResponse.json();

    if (checkResult.message === "地點已存在資料庫") {
      // 如果資料已存在，直接提示成功，不需再次儲存
      Swal.fire({
        icon: "warning",
        title: "資料已存在",
        text: "此地點資料已在後端中儲存。",
      });
      console.log("地點資料已存在於後端:", checkResult.placeInfo);
      console.log("地點資料已存在於後端:", details);
    } else {
      // 如果資料不存在，則儲存地點資訊
      const saveResponse = await fetch("http://localhost:8080/api/savePlace", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formattedPlaceDetails),
      });

      if (!saveResponse.ok) {
        throw new Error(`伺服器錯誤: ${saveResponse.statusText}`);
      }

      const saveResult = await saveResponse.json();
      Swal.fire({
        icon: "success",
        title: "成功",
        text: "地點已成功傳入後端!",
      });
      console.log("地點已成功傳入後端:", saveResult);
    }
  } catch (error) {
    console.error("處理地點資訊時出錯:", error);
    Swal.fire({
      icon: "error",
      title: "錯誤",
      text: `處理地點資訊時出現錯誤: ${error.message}`,
    });
  }
};


// 初始化 Autocomplete 並設置監聽
onMounted(() => {
  initAutocomplete();
});
*/
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
  background-color: #4caf50;
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
