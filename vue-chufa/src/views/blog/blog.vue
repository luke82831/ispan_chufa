<template>
<div class="form-container">
  <h1>發表新文章</h1>
  <div class="form-group">
    <label for="title">標題</label>
    <input type="text" id="title" name="title" placeholder="請輸入文章標題" v-model="title">
  </div>
  <div class="form-group">
    <label for="content">內容</label>
    <textarea id="content" name="content" placeholder="請輸入文章內容" v-model="content"></textarea>
  </div>
  <div class="form-group">
    <label for="image">上傳圖片</label>
    <input type="file" id="image" name="image" accept="image/*">
  </div>
  <div class="form-group">
    <button type="submit" @click="post()">發表文章</button>
  </div>
</div>
</template>

<script setup>
  import axiosapi from '@/plugins/axios.js';
  import { ref } from 'vue';
  const title = ref("")
  const content = ref("")

  const post = async () => {
    try {
      const body = {
        "postTitle":title.value,
        "postContent":content.value,
        "postLink":"",
        "userid":"1" //登入功能完成後從user取
      };
      const response = await axiosapi.post('/post/create',body);
      alert(response.data.message);
    } catch (error) {
      alert(error);
    }
  }
</script>

<style>
  body {
    font-family: Arial, sans-serif;
    line-height: 1.6;
    margin: 20px;
    background-color: #f9f9f9;
    color: #333;
  }
  .form-container {
      max-width: 600px;
      margin: 0 auto;
      background: #fff;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
  .form-container h1 {
      text-align: center;
      margin-bottom: 20px;
  }
  .form-group {
      margin-bottom: 15px;
  }
  .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
  }
  .form-group input,
  .form-group textarea {
      width: 100%;
      padding: 10px;
      border: 1px solid #ccc;
      border-radius: 4px;
      font-size: 1em;
  }
  .form-group textarea {
      resize: vertical;
      min-height: 150px;
  }
  .form-group button {
      width: 100%;
      padding: 10px;
      background-color: #007bff;
      color: #fff;
      border: none;
      border-radius: 4px;
      font-size: 1em;
      cursor: pointer;
  }
  .form-group button:hover {
      background-color: #0056b3;
  }
</style>