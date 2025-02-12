<template>
  <div class="place-management">
  <div><h1 class="page-title">地點管理</h1></div>
  <div class="container">
    <div v-if="loading" class="loading-text">載入中...</div>
    <div v-else-if="!places.length" class="no-data">沒有地點可顯示</div>
    <div v-else class="table-wrapper">
      <table class="place-table">
        <thead>
          <tr>
            <th class="number-col">ID</th>
            <th class="small-col">類型</th>
            <th class="mid-col">名稱</th>
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
            <th class="large-col">更改/刪除</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="place in places" :key="place.placeId" class="hover-effect">
            <td>{{ place.placeId }}</td>
            <td>{{ place.placeType }}</td>
            <td>{{ place.placeName }}</td>
            <td>
              <div v-if="place.photos.length">
                <img :src="place.photos[0]" alt="Place Photo" class="place-photo" />
              </div>
              <div v-else>無圖片</div>
            </td>
            <td><div class="scroll-container">{{ place.placeAddress }}</div></td>
            <td>{{ place.city }}</td>
            <td>{{ place.region }}</td>
            <td>{{ place.placePhone }}</td>
            <td><div class="scroll-container">{{ place.businessHours }}</div></td>
            <td>{{ place.rating }}</td>
            <td><a :href="place.website" target="_blank">連結</a></td>
            <td>{{ place.priceLevel }}</td>
            <td>{{ place.isClosed ? '休息中' : '營業中' }}</td>
            <td>
              <div class="action-buttons">

              <button class="edit-btn" @click="openEditModal(place)">更新</button>
              <button class="delete-btn" @click="confirmDelete(place.placeId)">刪除</button>
            </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>


    <!-- 分頁控制 -->
    <div class="pagination">
      <button :disabled="currentPage === 0" @click="fetchPlaces(currentPage - 1)">
        上一頁
      </button>
      <span>第 {{ currentPage + 1 }} 頁 / 共 {{ totalPages }} 頁</span>
      <button
        :disabled="currentPage === totalPages - 1"
        @click="fetchPlaces(currentPage + 1)"
      >
        下一頁
      </button>
    </div>


    <!-- 編輯模態框 -->
    <div v-if="showEditModal" class="modal">
      <div class="modal-content">
        <h3>編輯地點</h3>
        <label>類型:</label>
          <input v-model="editedPlace.placeType" type="text" class="input-box" />
        </div>

        <div class="input-group">
          <label>名稱:</label>
          <input v-model="editedPlace.placeName" type="text" class="input-box" />
        </div>

        <div class="input-group">
          <label>地址:</label>
          <input v-model="editedPlace.placeAddress" type="text" class="input-box" />
        </div>

        <div class="input-group">
          <label>城市:</label>
          <input v-model="editedPlace.city" type="text" class="input-box" />
        </div>

        <div class="input-group">
          <label>地區:</label>
          <input v-model="editedPlace.region" type="text" class="input-box" />
        </div>

        <div class="input-group">
          <label>電話:</label>
          <input v-model="editedPlace.placePhone" type="text" class="input-box" />
        </div>

        <div class="input-group">
          <label>評分:</label>
          <input v-model="editedPlace.rating" type="number" step="0.1" min="0" max="5" class="input-box" />
        </div>

        <div class="input-group">
          <label>價錢:</label>
          <input v-model="editedPlace.priceLevel" type="number" min="0" class="input-box" />
        </div>

        <div class="input-group">
          <label>狀態:</label>
          <select v-model="editedPlace.isClosed" class="input-box">
            <option :value="false">營業中</option>
            <option :value="true">休息中</option>
          </select>
        </div>

        <div class="modal-buttons">
          <button @click="saveChanges" class="save-btn">儲存</button>
          <button @click="closeEditModal" class="cancel-btn">取消</button>


        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";

const places = ref([]);
const loading = ref(true);
const showEditModal = ref(false);
const editedPlace = ref({});
const currentPage = ref(0); // 當前頁碼
const totalPages = ref(0); // 總頁數



// 取得地點資料（支援分頁）
const fetchPlaces = async (page = 0) => {
  if (page < 0 || (totalPages.value && page >= totalPages.value)) {
    return;
  }

  loading.value = true;
  try {
    const response = await axios.get(`/api/places/paged?page=${page}&size=4`);
    places.value = response.data.content; // 地點列表
    currentPage.value = response.data.number; // 當前頁碼
    totalPages.value = response.data.totalPages; // 總頁數
  } catch (error) {
    console.error("❌ 無法取得地點資料:", error);
  } finally {
    loading.value = false;
  }
};

const openEditModal = (place) => {
  editedPlace.value = { ...place };
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
};

const saveChanges = async () => {
  try {
    await axios.put(`/api/places/${editedPlace.value.placeId}`, editedPlace.value);
    fetchPlaces();
    closeEditModal();
    Swal.fire("成功", "地點資訊已更新", "success");
  } catch (error) {
    console.error("❌ 更新失敗:", error);
  }
};

const confirmDelete = async (placeId) => {
  const result = await Swal.fire({
    title: "確定要刪除嗎？",
    text: "刪除後無法復原！",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#d33",
    cancelButtonColor: "#3085d6",
    confirmButtonText: "刪除",
    cancelButtonText: "取消",
  });

  if (result.isConfirmed) {
    try {
      await axios.delete(`/api/places/${placeId}`);
      places.value = places.value.filter(place => place.placeId !== placeId);
      Swal.fire("已刪除", "地點已成功刪除", "success");
    } catch (error) {
      console.error("❌ 刪除失敗:", error);
      Swal.fire("錯誤", "刪除失敗，請稍後再試", "error");
    }
  }
};


onMounted(fetchPlaces);

</script>

<style scoped>


/*------------------------主頁面功能----------------*/



.container {
  max-width: 100%;
  height: 100vh; /* 讓容器佔滿整個視窗 */
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* 讓內容靠右 */
  padding: 20px;
  overflow-x: hidden;
  padding-top: 20px; /* 增加上方間距，給標題留空間 */
  width: 100%;
  padding: 20px;

}


.place-management {
  width: 100%;
  max-width: 2000px;
  padding: 30px;
  background: white;
  border-radius: 15px;            /* 使用文章管理的圓角 */
  box-shadow:
    0 10px 30px rgba(0, 0, 0, 0.2),
    0 5px 15px rgba(0, 0, 0, 0.15);
  font-family: "Microsoft JhengHei", sans-serif;

}

.table-wrapper {
  width: 100%; /* 讓表格內容決定寬度 */
  max-width: 100vw;
  overflow-x: auto;
  display: block;
  justify-content: flex-end; /* 讓表格靠右 */
  margin-bottom: 20px; /* ✅ 讓表格和分頁有間距 */
  border-radius: 15px;
}

.place-table {
  width: 100%; /* 讓表格寬度適應內容 */
  min-width: 1200px; /* 確保至少填滿畫面 */
  border-collapse: collapse;
  table-layout: fixed; /* 讓內容自然分配寬度 */
}


/*框線*/
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

.edit-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}
.edit-btn:hover {
  background-color: #45a049;
}
.delete-btn {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}
.delete-btn:hover {
  background-color: #d32f2f;
}

/* 縮小特定欄位 */
.small-col {
  width: 80px;
}

/* 放大名稱和照片欄位 */
.large-col {
  width: 150px;
}

/* 更新/刪除 */
.mid-col {
  width: 110px;
}


/* 放大電話欄位 */
.phone-col {
  width: 100px;
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


.page-title {
  text-align: center;
  font-size: 28px;
  color: #343a40;
  border-bottom: 2px solid #ccc;
  margin-bottom: 0px;

}


.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 移除滾動條 */
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto; /* 讓內容可滾動，但頁面不滾動 */
  padding: 20px;
}


/* 確保 更新 和 刪除 按鈕是並排對齊 */
.action-buttons {
  display: inline-flex;  /* 讓按鈕維持 inline 並排 */
  justify-content: center; /* 置中對齊 */
  align-items: center; /* 垂直對齊 */
  gap: 8px; /* 調整按鈕間距 */
  white-space: nowrap; /* 防止換行 */
  min-width: 150px; /* 確保欄位不會過窄 */
}

/* 讓按鈕大小一致 */
.action-buttons button {
  min-width: 70px; /* 避免按鈕大小變化 */
  height: 36px !important; /* 統一按鈕高度 */
  padding: 6px 12px;
  text-align: center;
  font-size: 14px; /* 統一字體大小 */
  display: flex;
  align-items: center;
  justify-content: center;
  align-self: flex-start;
}




/*------------------------編輯頁面----------------*/



.modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(200, 200, 200, 0.5);
  z-index: 1000;
  width: 90%;
  max-width: 500px;
  animation: fadeIn 0.2s ease-out;
}

.modal-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: center;
  color: #333;
}

/* 每個輸入欄位的容器 */
.input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

/* 輸入框的標籤 */
.input-group label {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #555;
}

/* 欄位輸入框 (明顯的灰色長方形) */
.input-box {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 2px solid #ccc;
  border-radius: 6px;
  background-color: #f8f8f8;
  transition: all 0.3s ease;
}

/* 聚焦時，邊框變藍色 */
.input-box:focus {
  border-color: #007bff;
  background-color: white;
  outline: none;
}

/* 狀態選擇的下拉框 */
.input-box select {
  cursor: pointer;
}

/* 按鈕區塊 */
.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

/* 儲存按鈕 */
.save-btn {
  flex: 1;
  padding: 12px;
  font-size: 16px;
  border: none;
  border-radius: 6px;
  color: white;
  background: #28a745;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-right: 10px;
}

.save-btn:hover {
  background: #218838;
}

/* 取消按鈕 */
.cancel-btn {
  flex: 1;
  padding: 12px;
  font-size: 16px;
  border: none;
  border-radius: 6px;
  color: white;
  background: #dc3545;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: #c82333;
}

/* 彈出動畫 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translate(-50%, -48%);
  }
  to {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}


/*------------------------分頁功能----------------*/


.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin-top: 20px;
  gap: 10px;
  font-family: "Microsoft JhengHei", sans-serif;
  text-align: center;
}

.pagination button {
  padding: 8px 16px;
  border: 2px solid #ccc;
  border-radius: 50px;
  background: white;
  color: #333;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;


}

.pagination button:hover {
  border-color: #007bff;
  color: #007bff;
  transform: scale(1.1);

}

.pagination button:disabled {
  background: #f0f0f0;
  color: #999;
  border: 2px solid #e0e0e0;
  cursor: not-allowed;
  transform: none;
}

.pagination span {
  font-size: 14px;
  color: #555;
  font-weight: bold;
}





/*------------------------hover功能----------------*/



/* 滑鼠懸停時的行高亮顯示 */
.hover-effect {
  transition: background-color 0.3s ease-in-out;
  position: relative; /* 確保內部的按鈕不會受到影響 */
}


/* 滑鼠移上去變成淡綠色 */
.hover-effect:hover {
  background-color: #f0f8e6 !important;
}

/* 確保按鈕在 hover 時不變色 */
.hover-effect td:last-child {
  position: relative;
  z-index: 10;
}


.edit-btn, .delete-btn {
  position: relative;
  z-index: 11;
}

/* 按鈕的原始樣式 */
.edit-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit-btn:hover {
  background-color: #0056b3;
}

.delete-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: -10px;
}

.delete-btn:hover {
  background-color: #c82333;
}


</style>
