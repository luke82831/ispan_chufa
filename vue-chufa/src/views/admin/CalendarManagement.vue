<template>
  <div class="calendar-management">
    <!-- 標題區塊：標題置中，Import JSON 按鈕置右 -->
    <div class="header">
      <h1 class="page-title">日期管理</h1>
      <div class="import-section">
        <input
          type="file"
          ref="fileInput"
          @change="handleFileUpload"
          accept="application/json"
          style="display: none"
        />
        <button class="import-btn" @click="triggerFileInput">匯入 JSON 檔案</button>
      </div>
    </div>

    <!-- 長線條，與 PlaceManagement 一致 -->
    <div class="divider"></div>

    <!-- 資料列表區塊 -->
    <div class="container">
      <div v-if="loading" class="loading-text">載入中...</div>
      <div v-else-if="!calendars.length" class="no-data">沒有行事曆資料可顯示</div>
      <div v-else class="table-wrapper">
        <table class="calendar-table">
          <thead>
            <tr>
              <th>日期</th>
              <th>星期</th>
              <th>放假日</th>
              <th>備註</th>
            </tr>
          </thead>
          <tbody>
            <!-- 直接用 calendars 取代 paginatedCalendars -->
            <tr v-for="calendar in calendars" :key="calendar.date">
              <td>{{ calendar.date }}</td>
              <td>{{ calendar.week }}</td>
              <td>{{ calendar.isHoliday ? '是' : '否' }}</td>
              <td>{{ calendar.description }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <!-- 分頁功能已移除 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/plugins/axios.js'
import Swal from 'sweetalert2'

const calendars = ref([])
const loading = ref(false)
const fileInput = ref(null)

// 取得所有行事曆資料
const fetchCalendars = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/calendars')
    calendars.value = response.data
  } catch (error) {
    console.error("❌ 無法取得行事曆資料:", error)
    Swal.fire("錯誤", "無法取得行事曆資料", "error")
  } finally {
    loading.value = false
  }
}

// 點選按鈕時觸發檔案選擇
const triggerFileInput = () => {
  fileInput.value.click()
}

// 處理 JSON 檔案上傳
const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      const jsonData = JSON.parse(e.target.result)
      console.log("📜 解析的 JSON:", jsonData)

      await axios.post('/api/calendars/import', jsonData, {
        headers: { 'Content-Type': 'application/json' }
      })

      Swal.fire("成功", "行事曆資料已匯入", "success")
      fetchCalendars() // 重新取得行事曆資料
    } catch (error) {
      console.error("❌ 匯入 JSON 檔案失敗:", error)
      Swal.fire("錯誤", "匯入資料失敗", "error")
    }
  }
  reader.readAsText(file) // 讀取 JSON 檔案
}

onMounted(() => {
  fetchCalendars()
})
</script>

<style scoped>
.calendar-management {
  padding: 30px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2), 0 5px 15px rgba(0,0,0,0.15);
  font-family: "Microsoft JhengHei", sans-serif;
  
}

/* 讓標題置中 */
.header {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 10px;
}

/* 確保 Import JSON 按鈕固定在右側 */
.import-section {
  position: absolute;
  right: 0;
}

/* 加長標題下方的線條，與 PlaceManagement 一致 */
.divider {
  width: 100%;
  height: 2px;
  background-color: #ccc;
  margin-bottom: 20px;
}

.page-title {
  font-size: 28px;
  color: #343a40;
  text-align: center;
}

/* Import JSON 按鈕縮小 */
.import-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 6px 12px;  /* 調小按鈕 */
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;  /* 縮小字體 */
}

.import-btn:hover {
  background-color: #0056b3;
}

.container {
  /* 也可改成更大高度或以視情況而定 */
  max-height: 600px; 
  overflow-y: auto;  
  max-width: 100%;
  margin-top: 10px;
}

.table-wrapper {
  width: 100%;
  border-radius: 15px;
}

.calendar-table {
  width: 100%;
  border-collapse: collapse;
}

.calendar-table th, .calendar-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.calendar-table th {
  background-color: #f4f4f4;
}

.loading-text, .no-data {
  text-align: center;
  font-size: 18px;
  padding: 20px;
}
</style>
