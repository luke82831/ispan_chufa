<template>
  <div>
    <h1>創建行程規劃</h1>
  </div>
  <form @submit.prevent="handleSubmit">
    <!-- 表單區塊 -->
    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="itinerary-form">
        <div class="form-group">
          <label for="coverPhoto">封面照片：</label>
          <input type="file" @change="onFileChange" id="coverPhoto" class="input-file" />
        </div>

        <div class="form-group">
          <label for="itinerary-name">行程名稱：</label>
          <input
            type="text"
            id="itinerary-name"
            v-model="formData.name"
            class="input-text"
          />
        </div>

        <div class="form-group">
          <label for="start-date">開始日期：</label>
          <input
            type="date"
            id="start-date"
            v-model="formData.startDate"
            class="input-date"
          />
        </div>

        <div class="form-group">
          <label for="end-date">結束日期：</label>
          <input
            type="date"
            id="end-date"
            v-model="formData.endDate"
            class="input-date"
          />
        </div>

        <button type="submit" class="submit-button">建立行程</button>
      </form>
    </div>
  </form>
</template>

<script>
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";

export default {
  data() {
    return {
      formData: {
        name: "",
        startDate: "",
        endDate: "",
        coverPhoto: null,
      },
      userId: null, // 從使用者登入後取得的 userId
    };
  },
  methods: {
    // 獲取會員資料並設置 userId
    async fetchProfile() {
      console.log("Fetching profile...");
      try {
        const response = await axios.get("/ajax/secure/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        console.log("Profile fetch response:", response);
        if (response.data.success) {
          this.userId = response.data.user?.userid;
          console.log("Fetched userId:", this.userId);
        } else {
          console.error("Fetch profile failed:", response.data.message);
          Swal.fire("錯誤", response.data.message, "error");
        }
      } catch (error) {
        console.error("Fetch profile failed:", error);
        Swal.fire("錯誤", "無法獲取會員資料", "error");
      }
    },

    // 確保在獲取到會員資料後再進行後續操作
    async handleSubmit() {
      console.log("Submit clicked, preparing FormData...");

      if (!this.userId) {
        console.log("User ID not found, aborting submit.");
        Swal.fire("錯誤", "無法找到使用者ID", "error");
        return;
      }

      // 檢查 coverPhoto 是否存在
      if (!this.formData.coverPhoto) {
        console.log("Cover photo is missing.");
        Swal.fire("錯誤", "封面照片是必填的", "error");
        return;
      }

      // 構建 FormData
      const formData = new FormData();
      formData.append("coverPhoto", this.formData.coverPhoto);
      formData.append("tripName", this.formData.name);
      formData.append("startDate", this.formData.startDate);
      formData.append("endDate", this.formData.endDate);
      formData.append("userId", this.userId); // 只保留這個 userId

      // Log FormData content
      for (let [key, value] of formData.entries()) {
        console.log(`${key}:`, value);
      }

      console.log("Sending request...");

      try {
        const response = await axios.post("/api/schedule", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
        console.log("行程已成功建立", response.data);
        Swal.fire("成功", "行程已成功建立", "success");

        // 儲存行程資料至 localStorage
        localStorage.setItem("itineraryData", JSON.stringify(this.formData));

        // 導航到 Map.vue 頁面
        this.$router.push("/planningtabs");
      } catch (error) {
        console.error("發生錯誤", error);
        const errorMessage = error.response?.data?.message || "發生未知錯誤";
        Swal.fire({
          icon: "error",
          title: "錯誤",
          text: errorMessage,
        });
      }
    },

    // 處理檔案變更
    onFileChange(event) {
      const file = event.target.files[0];
      console.log("File selected:", file);

      if (!file) {
        console.log("No file selected");
        return;
      }

      if (file.size > 10 * 1024 * 1024) {
        alert("文件大小超過限制");
        return;
      }

      this.formData.coverPhoto = file;
      // 檢查檔案是否存在
      console.log("Cover photo file:", this.formData.coverPhoto);
    },
  },

  created() {
    console.log("Component created, fetching profile...");
    this.fetchProfile(); // 在組件創建時調用以獲取會員資料
  },
};
</script>

<style scoped>
/* 標題獨立區塊樣式 */
.title-container {
  padding: 20px;
  text-align: center;
  border-radius: 8px;
  color: rgb(27, 27, 27); /* 字體顏色 */
  margin-bottom: 10px 0px 10px 0px; /* 與表單的間距 */
}

.title {
  font-size: 2.5em;
  margin: 0;
  font-weight: bold;
}

/* 表單區塊 */
.form-container {
  width: 80%;
  margin: 0 auto;
  padding: 20px;
  max-width: 600px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* 表單樣式 */
.itinerary-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  font-size: 1em;
  margin-bottom: 5px;
  color: #555;
}

input {
  padding: 10px;
  font-size: 1em;
  border: 1px solid #ccc;
  border-radius: 5px;
  transition: border-color 0.3s ease;
}

input:focus {
  border-color: #007bff;
  outline: none;
}

/* 按鈕樣式 */
.submit-button {
  padding: 12px 20px;
  background-color: #007bff;
  color: white;
  font-size: 1.2em;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #0056b3;
}
</style>
