<template>
  <div class="container">
    <div v-if="loading" class="loading-text">載入中...</div>
    <div v-else-if="!places.length" class="no-data">沒有地點可顯示</div>
    <div v-else class="table-wrapper">
      <table class="place-table">
        <thead>
          <tr>
            <th class="number-col">ID</th>
            <th class="small-col">類型</th>
            <th class="large-col">名稱</th>
            <th class="large-col">照片</th>
            <th class="scroll-col">地址</th>
            <th class="xsmall-col">城市</th>
            <th class="xsmall-col">地區</th>
            <th class="phone-col">電話</th>
            <th class="scroll-col">營業時間</th>
            <th class="number-col">評分</th>
            <th class="number-col">網址</th>
            <th class="xsmall-col">價錢</th>
            <th class="xsmall-col">狀態</th>
            <th class="small-col">更改/刪除</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="place in places" :key="place.placeId">
            <td>{{ place.placeId }}</td>
            <td>{{ place.placeType }}</td>
            <td>{{ place.placeName }}</td>
            <td>
              <div v-if="place.photos.length">
                <img :src="place.photos[0]" alt="Place Photo" class="place-photo" />
              </div>
              <div v-else>無圖片</div>
            </td>
            <!-- 地址改為橫向滾動 -->
            <td>
              <div class="scroll-container">{{ place.placeAddress }}</div>
            </td>
            <td>{{ place.city }}</td>
            <td>{{ place.region }}</td>
            <td>{{ place.placePhone }}</td>
            <!-- 營業時間改為橫向滾動 (使用原始值) -->
            <td>
              <div class="scroll-container">
                {{ place.businessHours }}
              </div>
            </td>
            <td>{{ place.rating }}</td>
            <td><a :href="place.website" target="_blank">連結</a></td>
            <td>{{ place.priceLevel }}</td>
            <td>{{ place.isClosed ? '休息中' : '營業中' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "@/plugins/axios.js";

// 定義地點類型
interface Place {
  placeId: number;
  googlemapPlaceId: string;
  placeType: string;
  placeName: string;
  placeAddress: string;
  city: string;
  region: string;
  longitude: number;
  latitude: number;
  placePhone: string;
  businessHours: string;
  placeInfo: string;
  rating: number;
  website: string;
  bookingUrl: string;
  priceLevel: number;
  accommodationType: string;
  reservation: boolean;
  isClosed: boolean;
  photos: string[];
}

const places = ref<Place[]>([]);
const loading = ref<boolean>(true);

// 取得地點資料
const fetchPlaces = async () => {
  try {
    const response = await axios.get("/api/places");
    console.log("📢 取得地點資料:", response.data);
    places.value = response.data;
  } catch (error) {
    console.error("❌ 無法取得地點資料:", error);
    places.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(fetchPlaces);
</script>

<style scoped>
.container {
  max-width: 100%;
  padding: 20px;
  overflow-x: auto;
}
.table-wrapper {
  width: 100%;
  overflow-x: auto;
}
.place-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed; /* 避免欄位過寬 */
}
.place-table th,
.place-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.place-table th {
  background-color: #f4f4f4;
}
.loading-text,
.no-data {
  text-align: center;
  font-size: 18px;
}
.place-photo {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

/* 縮小特定欄位 */
.small-col {
  width: 80px;
}

/* 放大名稱和照片欄位 */
.large-col {
  width: 150px;
}

/* 放大電話欄位 */
.phone-col {
  width: 120px;
}

/* 縮小價錢和狀態欄位 */
.xsmall-col {
  width: 60px;
}

/* 給數字使用的欄位 */
.number-col {
  width: 50px;
}

/* 滾動區域 */
.scroll-container {
  max-width: 200px; /* 設定最大寬度，超出時可滾動 */
  overflow-x: auto;
  white-space: nowrap;
  background: #f9f9f9;
  padding: 5px;
  border-radius: 4px;
  display: inline-block;
}

/* 滾動欄位 */
.scroll-col {
  width: 200px;
  max-width: 200px;
}
</style>
