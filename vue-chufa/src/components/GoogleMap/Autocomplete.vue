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
    const suggestions = ref([]); // 儲存建議

    // 初始化 Autocomplete
    const initAutocomplete = async () => {
      try {
        const { Autocomplete } = await google.maps.importLibrary("places");

        const autocomplete = new Autocomplete(
          document.getElementById("search-input"),
          {
            fields: [
            "place_id",
            "geometry", 
            "name",
            "formatted_address",
            "rating",
            "opening_hours",
            "photos",
            "rating",
            "geometry"
            ],
          }
        );
        autocomplete.setOptions({
          bounds: new google.maps.LatLngBounds(
            new google.maps.LatLng(21.846, 119.534),
            new google.maps.LatLng(25.382, 122.006)
          ),
          strictBounds: true,
        });

        autocomplete.addListener("place_changed", () => {
          const place = autocomplete.getPlace();

          // 確保地點有效且包含經緯度
          if (place && place.geometry && place.place_id) {
            // 打印出 PLACE ID
            console.log("Place ID:", place.place_id);

            // 傳遞選擇的地點給父組件，包含 placeId
            props.onPlaceSelected(place);
          }
        });
      } catch (error) {
        console.error("Autocomplete 初始化失敗:", error);
      }
    };

    return {
      searchInput,
      suggestions,
      initAutocomplete,
    };
  },

  mounted() {
    this.initAutocomplete();
  },
};
</script>

<style scoped>
  .search-section input {
    width: 80%; /* 調整搜尋框寬度 */
    padding: 12px;
    font-size: 18px;
    margin-bottom: 15px;
    border-radius: 5px;
    border: 1px solid #ccc;
  }
</style>