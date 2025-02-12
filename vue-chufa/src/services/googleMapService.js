// googleMapService.js
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios";

// 初始化 Autocomplete
export async function initAutocomplete(
  searchInputId,
  placeStore,
  emit,
  checkPlaceInBackend,
  fetchPlaceDetailsFromGoogle
) {
  try {
    const { Autocomplete } = await google.maps.importLibrary("places");

    const autocomplete = new Autocomplete(
      document.getElementById(searchInputId),
      {
        fields: ["place_id", "address_components", "geometry", "name"],
        types: ["establishment"],
      }
    );

    autocomplete.setOptions({
      componentRestrictions: { country: "TW" },
    });

    let previousPlaceId = null;

    autocomplete.addListener("place_changed", async () => {
      const place = autocomplete.getPlace();

      if (!place || !place.place_id) {
        console.error("❌ 無效的地點或缺少 `place_id`:", place);
        return;
      }

      if (place.place_id === previousPlaceId) {
        console.log("✅ 選擇的是相同地點，跳過更新標記");
        return;
      }
      previousPlaceId = place.place_id;

      console.log("🔍 嘗試在後端檢查是否已有此地點:", place.place_id);

      // ✅ 檢查後端是否已有該地點
      let placeExists = await checkPlaceInBackend(place.place_id);

      if (placeExists) {
        console.log("✅ 後端已有該地點，使用後端資料:", placeExists);

        placeStore.selectedPlaceId = placeExists.placeId;
        placeStore.savePlaceToMap(placeExists);
        emit("place-selected", placeExists);
      } else {
        console.log("🔍 後端無此地點，從 Google 抓取完整資訊...");
        fetchPlaceDetailsFromGoogle(
          place,
          placeStore,
          emit,
          checkPlaceInBackend
        );
      }
    });
  } catch (error) {
    console.error("Autocomplete 初始化失敗:", error);
  }
}

// 從後端查詢地點資料
export async function checkPlaceInBackend(placeId) {
  try {
    const response = await axiosapi.post(`/api/checkPlace`, { placeId });

    const result = response.data;
    if (result.message === "地點已存在資料庫") {
      console.log("✅ 後端地點資料:", result.placeInfo);
      return result.placeInfo; // ✅ 只返回 `placeInfo`，不 `emit`
    }
    return null;
  } catch (error) {
    console.error("❌ 查詢後端地點資料時出錯:", error);
    return null;
  }
}

// 從 Google 取詳細資訊並提交到後端
export async function fetchPlaceDetailsFromGoogle(
  place,
  placeStore,
  emit,
  checkPlaceInBackend
) {
  const { PlacesService } = await google.maps.importLibrary("places");
  const service = new PlacesService(document.createElement("div"));
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
      submitToBackend(details, placeStore, emit, checkPlaceInBackend);
    } else {
      console.error("取得地點詳細資訊失敗:", status);
    }
  });
}

async function submitToBackend(details, placeStore, emit, checkPlaceInBackend) {
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

  // 格式化地點資料
  const convertPriceLevel = (priceLevel) => {
    switch (priceLevel) {
      case 0:
        return "便宜";
      case 1:
        return "平價";
      case 2:
        return "中等";
      case 3:
        return "高級";
      case 4:
        return "奢華";
      default:
        return null;
    }
  };

  // 格式化營業時間資料
  // const businessHours = (openingHours) => {
  //     if (!openingHours) return null;
  //     return openingHours.split("\n").reduce((acc, line) => {
  //         const [day, hours] = line.split(": ");
  //         if (day && hours) {
  //             acc[day.trim()] = hours.trim();
  //         }
  //         return acc;
  //     }, {});
  // };

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
    priceLevel:
      details.price_level !== undefined
        ? convertPriceLevel(details.price_level)
        : null,
    city: city || "未知城市",
    region: region || "未知地區",
    accommodationType: null, // 根據需求手動設定
    reservation: details.reservable || null,
    isClosed: details.is_permanently_closed || null,
  };

  try {
    const response = await axiosapi.post(
      `/api/savePlace`,
      formattedPlaceDetails
    );

    if (response.status === 200) {
      const result = response.data;
      console.log("地點已儲存:", result);

      const placeFromBackend = await checkPlaceInBackend(details.place_id);
      if (placeFromBackend) {
        console.log("✅ 後端已有該地點，使用後端資料:", placeFromBackend);
        placeStore.selectedPlaceId = placeFromBackend.placeId;
        placeStore.savePlaceToMap(placeFromBackend);
        emit("place-selected", placeFromBackend);
      } else {
        console.error("未找到後端資料");
      }
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
}
