<template>
    <div class="p-8 bg-gray-50 min-h-screen">
      <h1 class="text-2xl font-bold mb-6">关注列表</h1>
      <div v-if="followedList.length" class="grid gap-4">
        <div
          v-for="user in followedList"
          :key="user.userid"
          class="flex items-center p-4 bg-white rounded-lg shadow-md hover:shadow-lg transition"
        >
          <div class="w-16 h-16 bg-gray-200 rounded-full overflow-hidden">
            <img
              v-if="user.profilePicture"
              :src="user.profilePicture"
              alt="Profile Picture"
              class="w-full h-full object-cover"
            />
            <div v-else class="flex items-center justify-center h-full text-gray-500">
              <span class="text-xl">👤</span>
            </div>
          </div>
          <div class="ml-4">
            <h2 class="text-lg font-semibold">{{ user.nickname }}</h2>
            <p class="text-sm text-gray-600">{{ user.name }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-gray-500">暂无关注用户</div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import { ref, onMounted } from 'vue';
  
  export default {
    props: ['id'],
    setup(props) {
      const followedList = ref([]);
  
      const fetchFollowedList = async () => {
        try {
          const response = await axios.get(`http://localhost:8081/follow/followedList/${props.id}`);
          followedList.value = response.data;
        } catch (error) {
          console.error('获取关注列表失败:', error);
        }
      };
  
      onMounted(fetchFollowedList);
  
      return {
        followedList,
      };
    },
  };
  </script>
  
  <style scoped>
  img {
    border-radius: 50%;
  }
  </style>
  