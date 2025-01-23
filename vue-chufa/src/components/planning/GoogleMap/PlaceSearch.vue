<template>
  <div>
    <input
      id="search-input"
      type="text"
      v-model="searchInput"
      placeholder="請輸入地點"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from "vue";
import Swal from "sweetalert2"; // 確保引入 SweetAlert2
import { usePlaceStore } from "@/stores/placestore"; // 檢查路徑是否正確

const searchInput = ref(""); // 搜尋框輸入
const emit = defineEmits(["place-selected"]); // 定義 place-selected 事件
const API_URL = import.meta.env.VITE_API_URL; // 從環境變數讀取 API 基本路徑

// 使用 Pinia store
const placeStore = usePlaceStore();

// 初始化 Autocomplete
const initAutocomplete = async () => {
  try {
    const { Autocomplete } = await google.maps.importLibrary("places");

    const autocomplete = new Autocomplete(
      document.getElementById("search-input"),
      {
        fields: ["place_id", "address_components", "geometry", "name"], // 最小化初始欄位
        types: ["establishment"], // 只顯示地點名稱
      }
    );

    // 限制地點搜尋在台灣
    autocomplete.setOptions({
      componentRestrictions: { country: "TW" },
    });

    let previousPlaceId = null; // 用來儲存先前選擇的地點 ID

    autocomplete.addListener("place_changed", async () => {
      const place = autocomplete.getPlace();

      // 確保地點有效且包含 place_id
      if (place && place.place_id) {
        console.log("Place ID:", place.place_id);

        // 檢查是否為同一個地點，如果是則不做任何更新
        if (place.place_id === previousPlaceId) {
          console.log("選擇的是相同地點，跳過更新標記");
          return; // 若是同一地點，直接返回
        }

        // 更新已選地點 ID
        previousPlaceId = place.place_id;

        //檢查後端是否已有資料
        const placeExists = await checkPlaceInBackend(place.place_id);

        if (placeExists) {
          // 資料已存在，直接使用後端資料更新
          placeStore.setPlaceDetails(placeExists); // 更新 Pinia store 中的 placeDetails
          searchInput.value = placeExists.placeName; // 更新搜尋欄

          emit("place-selected", placeExists); // 傳遞資料給父組件
          console.log("後端資料傳遞");
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
    const response = await fetch(`${API_URL}/api/checkPlace`, {
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
const fetchPlaceDetailsFromGoogle = async (place) => {
  const { Autocomplete, PlacesService } = await google.maps.importLibrary(
    "places"
  );
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

  // 格式化地點type資料
  const lodgingTypes = [
    "bed_and_breakfast",
    "budget_japanese_inn",
    "campground",
    "camping_cabin",
    "cottage",
    "extended_stay_hotel",
    "farmstay",
    "guest_house",
    "hostel",
    "hotel",
    "inn",
    "japanese_inn",
    "lodging",
    "mobile_home_park",
    "motel",
    "private_guest_room",
    "resort_hotel",
    "rv_park",
  ];

  const attractionTypes = [
    "art_gallery",
    "art_studio",
    "auditorium",
    "cultural_landmark",
    "historical_place",
    "monument",
    "museum",
    "performing_arts_theater",
    "sculpture",
    "library",
    "adventure_sports_center",
    "amphitheatre",
    "amusement_center",
    "amusement_park",
    "aquarium",
    "botanical_garden",
    "casino",
    "dog_park",
    "garden",
    "hiking_area",
    "historical_landmark",
    "marina",
    "national_park",
    "park",
    "plaza",
    "roller_coaster",
    "state_park",
    "tourist_attraction",
    "water_park",
    "wildlife_park",
    "zoo",
    "church",
    "hindu_temple",
    "mosque",
    "synagogue",
    "beach",
  ];

  const restaurantTypes = [
    "acai_shop",
    "afghani_restaurant",
    "african_restaurant",
    "american_restaurant",
    "asian_restaurant",
    "bagel_shop",
    "bakery",
    "bar",
    "bar_and_grill",
    "barbecue_restaurant",
    "brazilian_restaurant",
    "breakfast_restaurant",
    "brunch_restaurant",
    "buffet_restaurant",
    "cafe",
    "cafeteria",
    "candy_store",
    "cat_cafe",
    "chinese_restaurant",
    "chocolate_factory",
    "chocolate_shop",
    "coffee_shop",
    "confectionery",
    "deli",
    "dessert_shop",
    "diner",
    "dog_cafe",
    "donut_shop",
    "fast_food_restaurant",
    "fine_dining_restaurant",
    "food_court",
    "french_restaurant",
    "greek_restaurant",
    "hamburger_restaurant",
    "ice_cream_shop",
    "indian_restaurant",
    "indonesian_restaurant",
    "italian_restaurant",
    "japanese_restaurant",
    "juice_shop",
    "korean_restaurant",
    "lebanese_restaurant",
    "mediterranean_restaurant",
    "mexican_restaurant",
    "middle_eastern_restaurant",
    "pizza_restaurant",
    "pub",
    "ramen_restaurant",
    "restaurant",
    "sandwich_shop",
    "seafood_restaurant",
    "spanish_restaurant",
    "steak_house",
    "sushi_restaurant",
    "tea_house",
    "thai_restaurant",
    "turkish_restaurant",
    "vegan_restaurant",
    "vegetarian_restaurant",
    "vietnamese_restaurant",
    "wine_bar",
  ];

  const categorizePlaceType = (types) => {
    const categories = [];

    if (types.some((type) => lodgingTypes.includes(type))) {
      categories.push("旅宿");
    }
    if (types.some((type) => attractionTypes.includes(type))) {
      categories.push("景點");
    }
    if (types.some((type) => restaurantTypes.includes(type))) {
      categories.push("餐廳");
    }

    return categories.length > 0 ? categories.join(", ") : "未知類型";
  };

  // 提取並格式化資料
  const formattedPlaceDetails = {
    googlemapPlaceId: details.place_id || null,
    placeType: details.types ? categorizePlaceType(details.types) : null,
    placeName: details.name || null,
    placeAddress: details.formatted_address || null,
    longitude: details.geometry?.location?.lng() || null,
    latitude: details.geometry?.location?.lat() || null,
    photos: details.photos?.map((photo) => photo.getUrl()) || null,
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
    reservation: details.reservable || null,
    isClosed: details.is_permanently_closed || null,
  };

  try {
    const response = await fetch(`${API_URL}/api/savePlace`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formattedPlaceDetails),
    });

    if (response.ok) {
      const result = await response.json();
      // Swal.fire({
      //   icon: "success",
      //   title: "成功",
      //   text: "地點已成功儲存到後端!",
      // });
      console.log("地點已儲存:", result);

      // 儲存後重新抓取資料
      const placeFromBackend = await checkPlaceInBackend(details.place_id);
      if (placeFromBackend) {
        placeStore.setPlaceDetails(placeFromBackend); // 更新 Pinia store 中的 placeDetails
        searchInput.value = placeFromBackend.placeName; // 更新搜尋欄
        emit("place-selected", placeFromBackend); // 傳遞資料給父組件
        console.log("儲存後已更新前端資料並傳遞給父組件");
      } else {
        console.error("未找到後端資料");
      }
    } else {
      throw new Error(`伺服器錯誤: ${response.statusText}`);
    }
  } catch (error) {
    console.error("儲存地點資訊時出錯:", error);
    // 顯示錯誤提示
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
</style>
