<template>
  <div>
    <h1>文章編輯器</h1>
    <!-- Quill 編輯器 -->
    <div id="editor">
      <p>請輸入</p>
    </div>
    <!-- 提交按鈕 -->
    <button @click="submitArticle">提交文章</button>
  </div>
</template>

<script >
import Quill from "quill";
import "quill/dist/quill.snow.css"; // Quill 的樣式文件

export default {
  data() {
    return {
      quill: null, // Quill 編輯器實例
    };
  },
  mounted() {
    // 初始化 Quill
    this.quill = new Quill('#editor', {
      theme: "snow",
      modules: {
        toolbar: {
          container: [
            ["bold", "italic", "underline"], // 字體樣式
            [{ header: [1, 2, 3, false] }], // 標題級別
            [{ list: "ordered" }, { list: "bullet" }], // 列表
            ["image", "video"], // 嵌入圖片和視頻
          ],
          handlers: {
            image: this.imageHandler, // 自定義圖片上傳處理器
          },
        },
      },
    })
  },
  methods:{
    submitArticle(){
      console.log(this.quill.root.innerHTML)
    }
  },
};
</script>

<style>
#editor-container {
  border: 1px solid #ccc;
  margin-bottom: 16px;
}
</style>
