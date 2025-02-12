<template>
  <div id="member-management-page">
    <div class="member-management">
      <h1 class="page-title">會員管理</h1>
      <div v-if="loading" class="loader">載入中...</div>
      <div v-else class="table-container">
        <table class="user-table">
          <thead>
            <tr>
              <th>會員名稱</th>
              <th>身份</th>
              <th>電子郵件</th>
              <th>手機號碼</th>
              <th>性別</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.userid">
              <td class="name-cell">{{ user.name }}</td>
              <td>{{ user.role }}</td>
              <!-- 電子郵件欄位：限制顯示長度，並附上「編輯」按鈕 -->
              <td class="ll-cell">
                <span class="email-text">{{ user.email }}</span>
                <button
                  @click="updateEmail(user.userid, user.email)"
                  class="btn edit-email-btn"
                >
                  編輯
                </button>
              </td>
              <td>{{ user.phone_number }}</td>
              <td>{{ user.gender }}</td>
              <td class="action-cell">
                <button
                  v-if="user.role !== 'ADMIN'"
                  @click="updateRole(user.userid, 'ADMIN')"
                  class="btn admin-btn"
                >
                  設為管理員
                </button>
                <button
                  v-if="user.role !== 'USER'"
                  @click="updateRole(user.userid, 'USER')"
                  class="btn user-btn"
                >
                  設為會員
                </button>
                <button @click="deleteMember(user.userid)" class="btn delete-btn">
                  刪除會員
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <!-- 分頁控制 -->
        <div class="pagination">
          <button :disabled="currentPage === 0" @click="fetchUsers(currentPage - 1)">
            上一頁
          </button>
          <span>第 {{ currentPage + 1 }} 頁 / 共 {{ totalPages }} 頁</span>
          <button
            :disabled="currentPage === totalPages - 1"
            @click="fetchUsers(currentPage + 1)"
          >
            下一頁
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";

const users = ref([]); // 當前頁的會員資料
const loading = ref(false); // 資料加載狀態
const currentPage = ref(0); // 當前頁
const totalPages = ref(0); // 總頁數

// 獲取會員資料（支援分頁）
const fetchUsers = async (page = 0) => {
  if (isNaN(page) || page < 0) {
    console.error("Invalid page number:", page);
    page = 0;
  }
  loading.value = true;
  try {
    const response = await axios.get(`/ajax/secure/members?page=${page}&size=10`, {
      withCredentials: true,
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    if (response.data.success) {
      users.value = response.data.users;
      currentPage.value = response.data.currentPage;
      totalPages.value = response.data.totalPages;
    } else {
      Swal.fire("錯誤", response.data.message, "error");
    }
  } catch (error) {
    Swal.fire("錯誤", "無法獲取會員資料", "error");
    console.error("Fetch Users Error:", error);
  } finally {
    loading.value = false;
  }
};

// 更新會員角色
const updateRole = async (userId, newRole) => {
  try {
    const response = await axios.put(
      `/ajax/secure/members/${userId}/role`,
      { role: newRole },
      {
        withCredentials: true,
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      }
    );
    if (response.data.success) {
      Swal.fire("成功", "身份更新成功", "success");
      fetchUsers(currentPage.value);
    } else {
      Swal.fire("錯誤", response.data.message, "error");
    }
  } catch (error) {
    Swal.fire("錯誤", "身份更新失敗", "error");
    console.error("Update Role Error:", error);
  }
};

// 刪除會員
const deleteMember = async (userId) => {
  try {
    const response = await axios.delete(`/ajax/secure/members/${userId}`, {
      withCredentials: true,
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    if (response.data.success) {
      Swal.fire("成功", "會員已刪除", "success");
      fetchUsers(currentPage.value);
    } else {
      Swal.fire("錯誤", response.data.message, "error");
    }
  } catch (error) {
    Swal.fire("錯誤", "刪除會員失敗", "error");
    console.error("Delete Member Error:", error);
  }
};

// 更新電子郵件的方法
const updateEmail = async (userId, currentEmail) => {
  const { value: newEmail } = await Swal.fire({
    title: "修改電子郵件",
    input: "email",
    inputLabel: "請輸入新的電子郵件",
    inputValue: currentEmail,
    showCancelButton: true,
    confirmButtonText: "更新",
    cancelButtonText: "取消",
    inputValidator: (value) => {
      if (!value) {
        return "請輸入電子郵件";
      }
    },
  });

  if (newEmail) {
    try {
      const response = await axios.put(
        `/ajax/secure/members/${userId}/email`,
        { email: newEmail },
        {
          withCredentials: true,
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        }
      );
      if (response.data.success) {
        Swal.fire("成功", "電子郵件更新成功", "success");
        fetchUsers(currentPage.value);
      } else {
        Swal.fire("錯誤", response.data.message, "error");
      }
    } catch (error) {
      Swal.fire("錯誤", "電子郵件更新失敗", "error");
      console.error("Update Email Error:", error);
    }
  }
};

fetchUsers();
</script>

<style scoped>
/* ------------------------ 外層與主要容器 ------------------------ */
#member-management-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px;
  overflow-x: hidden;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.member-management {
  width: 100%;
  height: 100vh;
  max-width: 1200px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin: 0 auto;
  font-family: "Microsoft JhengHei", sans-serif;
}

.member-management h1 {
  text-align: center;
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.loader {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

/* ------------------------ 表格樣式 ------------------------ */
.table-container {
  width: 90%;
  max-width: 1000px;
  margin: 20px auto;
  background: #fff;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 10px 15px;
  text-align: center;
  font-size: 14px;
  border-bottom: 1px solid #ddd;
}

.user-table th {
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #555;
}

.hover-effect:hover {
  background-color: #f0f8e6;
}
.member-management h1 {
  text-align: center;
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.loader {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

/* 列 hover 效果 */
.user-table tbody tr:hover {
  background-color: #f1f7e9;
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-table tbody tr:nth-child(even) {
  background: #fafafa;
}

.page-title {
  text-align: center;
  font-size: 28px;
  color: #343a40;
  border-bottom: 2px solid #ccc;
  margin-bottom: 0px;
}

.edit-email-btn {
  background: #40bed1;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-email-btn:hover {
  background: #138496;
  transform: scale(1.05);
}

.email-cell {
  display: grid;
  grid-template-columns: 200px auto;
  align-items: center;
  gap: 10px;
}

.email-text {
  width: 200px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

/* 操作按鈕容器 */
.action-cell {
  display: flex; /* 讓按鈕水平排列 */
  justify-content: center; /* 水平居中對齊 */
  align-items: center; /* 垂直居中對齊 */
  flex-wrap: wrap; /* 如果按鈕過多，自動換行 */
  gap: 10px; /* 按鈕之間的間距 */
}

/* 通用按鈕樣式 */
.btn {
  display: inline-block;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: background-color 0.3s ease, transform 0.2s ease;
  text-align: center;
  width: 110px; /* 確保按鈕寬度一致 */
  height: 40px; /* 確保按鈕高度一致 */
}

/* 設為管理員按鈕 */
.admin-btn {
  background-color: #007bff;
  color: white;
}
.admin-btn:hover {
  background-color: #0056b3;
  box-shadow: 0 4px 10px rgba(0, 91, 187, 0.4);
  transform: scale(1.05);
}

/* 設為會員按鈕 */
.user-btn {
  background-color: #28a745;
  color: white;
}
.user-btn:hover {
  background-color: #218838;
  box-shadow: 0 4px 10px rgba(33, 136, 56, 0.4);
  transform: scale(1.05);
}

/* 刪除會員按鈕 */
.delete-btn {
  background-color: #dc3545;
  color: white;
  position: static; /* 確保按鈕不脫離表格布局 */
}
.delete-btn:hover {
  background-color: #c82333;
  box-shadow: 0 4px 10px rgba(200, 35, 51, 0.4);
  transform: scale(1.05);
}

/* ----------------------- 分頁控制 ----------------------- */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
  font-family: "Microsoft JhengHei", sans-serif;
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
  margin: 0 10px;
}
</style>