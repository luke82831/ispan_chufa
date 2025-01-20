<template>
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
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.userid">
            <td>{{ user.name }}</td>
            <td>{{ user.role }}</td>
            <td>{{ user.email }}</td>
            <td>
              <!-- 切換為管理員按鈕 -->
              <button
                v-if="user.role !== 'ADMIN'"
                @click="updateRole(user.userid, 'ADMIN')"
                class="btn admin-btn"
              >
                設為管理員
              </button>
              <!-- 切換為會員按鈕 -->
              <button
                v-if="user.role !== 'USER'"
                @click="updateRole(user.userid, 'USER')"
                class="btn user-btn"
              >
                設為會員
              </button>
              <!-- 刪除會員按鈕 -->
              <button
                @click="deleteMember(user.userid)"
                class="btn delete-btn"
              >
                刪除會員
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/plugins/axios.js';
import Swal from 'sweetalert2';

// 會員資料陣列
const users = ref([]);
// 是否載入中
const loading = ref(false);

/**
 * 取得所有會員資料
 */
const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/ajax/secure/members', {
      withCredentials: true,
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    });
    if (response.data.success) {
      users.value = response.data.users;
    } else {
      Swal.fire('錯誤', response.data.message, 'error');
    }
  } catch (error) {
    Swal.fire('錯誤', '無法獲取會員資料', 'error');
    console.error('Fetch Users Error:', error);
  } finally {
    loading.value = false;
  }
};

/**
 * 更新會員角色
 * @param {Number} userId - 會員 ID
 * @param {String} newRole - 新角色
 */
const updateRole = async (userId, newRole) => {
  try {
    const response = await axios.put(
      `/ajax/secure/profile/${userId}/role`,
      { role: newRole },
      {
        withCredentials: true,
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      }
    );
    if (response.data.success) {
      Swal.fire('成功', '角色更新成功', 'success');
      fetchUsers();
    } else {
      Swal.fire('錯誤', response.data.message, 'error');
    }
  } catch (error) {
    Swal.fire('錯誤', '角色更新失敗', 'error');
    console.error('Update Role Error:', error);
  }
};

/**
 * 刪除會員
 * @param {Number} userId - 會員 ID
 */
const deleteMember = async (userId) => {
  try {
    const response = await axios.delete(`/ajax/secure/members/${userId}`, {
      withCredentials: true,
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    });
    if (response.data.success) {
      Swal.fire('成功', '會員已刪除', 'success');
      fetchUsers();
    } else {
      Swal.fire('錯誤', response.data.message, 'error');
    }
  } catch (error) {
    Swal.fire('錯誤', '刪除會員失敗', 'error');
    console.error('Delete Member Error:', error);
  }
};

// 初始化抓取會員資料
fetchUsers();
</script>

<style scoped>
/* 外層容器，整體背景與排版 */
.member-management {
  max-width: 900px;
  margin: 40px auto;
  padding: 30px;
  font-family: "Microsoft JhengHei", sans-serif;
  background: linear-gradient(145deg, #ffffff, #f3f3f3);
  border-radius: 20px;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.member-management h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  color: #343a40;
}

/* 載入中文字樣 */
.loader {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}

/* 表格外容器，帶點 padding 以區隔邊界 */
.table-container {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 表格 */
.user-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: 10px;
  overflow: hidden; /* 讓邊角圓弧不被單元格破壞 */
  background: #fff;
}

/* 表頭 */
.user-table thead {
  background: linear-gradient(135deg, #f0f0f0, #e4e4e4);
}

.user-table th {
  padding: 14px 12px;
  font-weight: bold;
  font-size: 14px;
  color: #495057;
  border-bottom: 1px solid #ccc;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 表身 */
.user-table tbody tr {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.user-table td {
  padding: 14px 12px;
  border-bottom: 1px solid #ddd;
  text-align: center;
  color: #555;
  font-size: 14px;
}

.user-table tr:nth-child(even) {
  background-color: #fcfcfc;
}

/* 列 hover 效果 */
.user-table tbody tr:hover {
  background-color: #f1f7e9;
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 按鈕共用樣式 */
.btn {
  border: none;
  padding: 8px 14px;
  margin: 0 4px;
  cursor: pointer;
  font-size: 0.9rem;
  border-radius: 6px;
  transition: background-color 0.3s ease, box-shadow 0.3s ease, transform 0.2s ease;
  font-weight: bold;
  color: #fff;
}

.btn:hover {
  transform: scale(1.03);
}

/* 角色按鈕：管理員 */
.admin-btn {
  background-color: #007bff;
}

.admin-btn:hover {
  background-color: #0056b3;
  box-shadow: 0 4px 10px rgba(0, 91, 187, 0.4);
}

/* 角色按鈕：會員 */
.user-btn {
  background-color: #28a745;
}

.user-btn:hover {
  background-color: #218838;
  box-shadow: 0 4px 10px rgba(33, 136, 56, 0.4);
}

/* 刪除按鈕 */
.delete-btn {
  background-color: #dc3545;
}

.delete-btn:hover {
  background-color: #c82333;
  box-shadow: 0 4px 10px rgba(200, 35, 51, 0.4);
}
</style>
