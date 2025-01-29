<template>
  <div>
    <h1>創建行程規劃</h1>
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
        <input type="date" id="end-date" v-model="formData.endDate" class="input-date" />
      </div>

      <button type="submit" class="submit-button">建立行程</button>
    </form>
  </div>
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
      userId: null,
    };
  },

  created() {
    this.fetchProfile();
  },

  methods: {
    async fetchProfile() {
      try {
        const { data } = await axios.get("/ajax/secure/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (data.success) {
          this.userId = data.user?.userid;
        } else {
          this.showError(data.message);
        }
      } catch (error) {
        this.showError("無法獲取會員資料");
      }
    },

    showError(message) {
      Swal.fire("錯誤", message, "error");
    },

    async handleSubmit() {
      if (!this.userId || !this.formData.coverPhoto) {
        this.showError(this.userId ? "封面照片是必填的" : "無法找到使用者ID");
        return;
      }

      const formData = this.createFormData();

      try {
        const response = await axios.post("/api/schedule", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        Swal.fire("成功", "行程已成功建立", "success");
        localStorage.setItem("itineraryData", JSON.stringify(this.formData));
        this.$router.push("/planningtabs");
      } catch (error) {
        const errorMessage = error.response?.data?.message || "發生未知錯誤";
        this.showError(errorMessage);
      }
    },

    createFormData() {
      const formData = new FormData();
      formData.append("coverPhoto", this.formData.coverPhoto);
      formData.append("tripName", this.formData.name);
      formData.append("startDate", this.formData.startDate);
      formData.append("endDate", this.formData.endDate);
      formData.append("userId", this.userId);
      return formData;
    },

    onFileChange(event) {
      const file = event.target.files[0];
      if (file && file.size <= 10 * 1024 * 1024) {
        this.formData.coverPhoto = file;
      } else {
        alert("文件大小超過限制或未選擇檔案");
      }
    },
  },
};
</script>

<style scoped>
/* 容器設定 */
.itinerary-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 標題 */
h1 {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

/* 表單區塊 */
.form-group {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  margin-bottom: 5px;
  display: block;
  color: #555;
}

/* 輸入框 */
.input-file,
.input-text,
.input-date {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ddd;
  margin-top: 5px;
}

.input-file {
  padding: 6px;
}

.input-text:focus,
.input-date:focus {
  border-color: #0056b3;
  outline: none;
}

/* 按鈕 */
.submit-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #0056b3;
}

/* 表單錯誤訊息 */
.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
}

/* 輸入框與按鈕的間距 */
.form-group + .form-group {
  margin-top: 20px;
}

button[type="submit"] {
  margin-top: 20px;
}
</style>
