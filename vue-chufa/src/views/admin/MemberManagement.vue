<template>
  <div id="member-management-page">
    <div class="member-management">
      <h1>會員管理</h1>
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
              <td>{{ user.email }}</td>
              <!-- 新增手機號碼欄位 -->
              <td>{{ user.phone_number }}</td>
              <!-- 新增性別欄位 -->
              <td>{{ user.gender }}</td>
              <td class="action-cell">
                <!-- 設為管理員按鈕 -->
                <button
                  v-if="user.role !== 'ADMIN'"
                  @click="updateRole(user.userid, 'ADMIN')"
                  class="btn admin-btn"
                >
                  設為管理員
                </button>
                <!-- 設為會員按鈕 -->
                <button
                  v-if="user.role !== 'USER'"
                  @click="updateRole(user.userid, 'USER')"
                  class="btn user-btn"
                >
                  設為會員
                </button>
                <!-- 刪除會員按鈕 -->
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

fetchUsers();
</script>

<style scoped>
/* 整個頁面背景 */
#member-management-page {
  background: #f0f2f5;
  min-height: 100vh;
  padding: 20px;
}

/* 卡片容器 */
#member-management-page .member-management {
  max-width: 1400px;
  margin: 40px auto;
  padding: 30px;
  font-family: "Microsoft JhengHei", sans-serif;
  background: linear-gradient(145deg, #ffffff, #f3f3f3);
  border-radius: 20px;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 頁面標題 */
#member-management-page .member-management h1 {
  text-align: center;
  font-size: 32px;
  color: #343a40;
  border-bottom: 2px solid #ccc;
  padding-bottom: 10px;
  margin-bottom: 30px;
}

/* 載入中 */
#member-management-page .loader {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

/* 表格外層容器：加上水平捲軸 */
#member-management-page .table-container {
  background: linear-gradient(145deg, #ffffff, #f7f7f7);
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  border: 1px solid #e0e0e0;
  margin-top: 30px;
  overflow-x: auto;
}

/* 表格樣式 */
#member-management-page .user-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  /* 依照欄位數量調整最小寬度 */
  min-width: 1200px;
}

#member-management-page .user-table thead {
  background: linear-gradient(135deg, #f0f0f0, #e4e4e4);
}

#member-management-page .user-table th {
  padding: 14px 12px;
  font-weight: bold;
  font-size: 14px;
  color: #495057;
  border-bottom: 1px solid #ccc;
  text-transform: uppercase;
  letter-spacing: 1px;
  text-align: center;
}

#member-management-page .user-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #ddd;
  text-align: center;
  color: #555;
  font-size: 14px;
}

/* 會員名稱欄設定單行顯示 */
#member-management-page .user-table td.name-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

#member-management-page .user-table tr:nth-child(even) {
  background-color: #fcfcfc;
}

#member-management-page .user-table tbody tr:hover {
  background-color: #f1f7e9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 保留操作欄寬度限制（根據需求調整） */
#member-management-page .user-table th:last-child,
#member-management-page .user-table td:last-child {
  min-width: 350px;
}

/* 操作按鈕區塊 */
#member-management-page .action-cell {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

/* 統一按鈕樣式，固定寬度與重置預設 */
/* 加入 display:flex 與對齊設定，確保垂直與水平置中 */
#member-management-page .btn {
  border: none;
  padding: 8px 12px; /* ✅ 縮小內邊距 */
  margin: 2px; /* ✅ 讓按鈕靠近 */
  cursor: pointer;
  font-size: 14px; /* ✅ 統一字體 */
  border-radius: 6px; /* ✅ 調整圓角 */
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
  width: 110px; /* ✅ 統一按鈕寬度 */
  height: 38px; /* ✅ 統一按鈕高度 */
  display: flex;
  align-items: center;
  justify-content: center;

}

/* 管理員按鈕：藍色 */
#member-management-page .admin-btn {
  background-color: #007bff;
}
#member-management-page .admin-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
}

/* 會員按鈕：綠色 */
#member-management-page .user-btn {
  background-color: #28a745;
}
#member-management-page .user-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

/* 刪除按鈕：紅色 */
#member-management-page .delete-btn {
  background-color: #dc3545;
  margin-top: 5px;
}
#member-management-page .delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

/* 分頁控制 */
#member-management-page .pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
  font-family: "Microsoft JhengHei", sans-serif;
}

#member-management-page .pagination button {
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

#member-management-page .pagination button:hover {
  border-color: #007bff;
  color: #007bff;
  transform: scale(1.1);
}

#member-management-page .pagination button:disabled {
  background: #f0f0f0;
  color: #999;
  border: 2px solid #e0e0e0;
  cursor: not-allowed;
  transform: none;
}

#member-management-page .pagination span {
  font-size: 14px;
  color: #555;
  font-weight: bold;
  margin: 0 10px;
}
</style>