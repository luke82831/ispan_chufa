<template>
  <div>
    <h1>會員管理</h1>
    <div v-if="loading">載入中...</div>
    <table v-else>
      <thead>
        <tr>
          <th>會員名稱</th>
          <th>角色</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.userid">
          <td>{{ user.name }}</td>
          <td>{{ user.role }}</td>
          <td>
            <!-- 切換為管理員按鈕 -->
            <button v-if="user.role !== 'ADMIN'" @click="updateRole(user.userid, 'ADMIN')">設為管理員</button>
            <!-- 切換為會員按鈕 -->
            <button v-if="user.role !== 'USER'" @click="updateRole(user.userid, 'USER')">設為會員</button>
            <!-- 刪除會員按鈕 -->
            <button @click="deleteMember(user.userid)">刪除會員</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/plugins/axios.js';
import Swal from 'sweetalert2';

const users = ref([]);
const loading = ref(false);

const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/ajax/secure/profile', {
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

fetchUsers();
</script>
