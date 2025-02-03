<template>
  <div>
    <h1 v-if="isEditing">文章編輯器</h1>
    <h1 v-else>部落格文章</h1>

    <!-- 文章列表 -->
    <div v-if="!isEditing">
      <button @click="isEditing = true" class="px-4 py-2 bg-blue-500 text-white rounded">新增文章</button>
      <div v-for="blog in blogs" :key="blog.id" class="bg-white shadow-lg rounded-lg p-4">
        <h2 class="text-xl font-bold mb-2">{{ blog.title }}</h2>
        <p class="text-gray-600">{{ blog.content.substring(0, 100) }}...</p>
      </div>
    </div>

    <!-- 文章編輯器 -->
    <div v-else>
      <div id="editor"></div>
      <button @click="submitArticle" class="mt-4 px-4 py-2 bg-blue-500 text-white rounded">
        提交文章
      </button>
      <button @click="isEditing = false" class="mt-4 px-4 py-2 bg-gray-500 text-white rounded">
        取消
      </button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import Quill from "quill";
import "quill/dist/quill.snow.css";

export default {
  setup() {
    const isEditing = ref(false);
    const blogs = ref([]);

    const fetchBlogs = async () => {
      try {
        const response = await fetch("https://your-api-endpoint.com/blogs");
        blogs.value = await response.json();
      } catch (error) {
        console.error("Error fetching blogs:", error);
      }
    };

    onMounted(fetchBlogs);

    return { isEditing, blogs };
  },
};
</script>
