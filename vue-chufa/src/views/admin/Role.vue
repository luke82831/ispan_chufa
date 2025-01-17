<template>
    <div>
      <h1>會員管理</h1>
      <table>
        <thead>
          <tr>
            <th>會員名稱</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.name }}</td>
            <td>{{ user.role }}</td>
            <td>
              <button v-if="user.role !== 'ADMIN'" @click="updateRole(user.id, 'ADMIN')">
                設為管理員
              </button>
              <button v-if="user.role !== 'USER'" @click="updateRole(user.id, 'USER')">
                設為會員
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import axios from '@/plugins/axios.js';
  
  const users = ref([]);
  
  const fetchUsers = async () => {
    const response = await axios.get('/ajax/secure/members'); // 获取所有用户的接口
    users.value = response.data.users;
  };
  
  const updateRole = async (userId, newRole) => {
    try {
      const response = await axios.put(`/ajax/secure/members/${userId}/role`, { role: newRole });
      if (response.data.success) {
        alert('角色更新成功');
        fetchUsers(); // 刷新用户列表
      } else {
        alert(response.data.message);
      }
    } catch (error) {
      alert('角色更新失败');
    }
  };
  
  fetchUsers();
  </script>
  