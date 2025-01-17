// src/components/PlanningForm.vue
<template>
  <div class="planning-form">
    <h2>行程設定</h2>
    <div class="cover-photo">
      <img :src="coverPhoto" alt="封面照片">
      <button @click="changePhoto">更換</button>
    </div>
    <div class="form-group">
      <label for="trip-name">行程名稱</label>
      <input type="text" id="trip-name" v-model="tripName" placeholder="幫行程取個名字吧">
    </div>
    <div class="form-group">
      <label for="trip-dates">行程日期</label>
      <div class="date-picker">
        <input type="date" v-model="startDate">
        <span>→</span>
        <input type="date" v-model="endDate">
      </div>
    </div>
    <div class="form-group">
      <label for="transportation">主要交通方式</label>
      <select v-model="transportation">
        <option value="auto">自訂</option>
        <option value="plane">飛機</option>
        <option value="train">火車</option>
      </select>
    </div>
    <div class="form-actions">
      <button @click="cancel">取消</button>
      <button @click="submitForm">完成</button>
    </div>
  </div>
</template>

<script>
import { postData } from '../services/api';

export default {
  data() {
    return {
      coverPhoto: 'path/to/default/cover.jpg',
      tripName: '',
      startDate: '',
      endDate: '',
      transportation: 'auto'
    };
  },
  methods: {
    changePhoto() {
      alert('更換照片功能未實現');
    },
    cancel() {
      this.resetForm();
    },
    submitForm() {
      postData({
        tripName: this.tripName,
        startDate: this.startDate,
        endDate: this.endDate,
        transportation: this.transportation
      })
      .then(response => {
        console.log('表單提交成功', response);
      })
      .catch(error => {
        console.error('提交失敗', error);
      });
    },
    resetForm() {
      this.tripName = '';
      this.startDate = '';
      this.endDate = '';
      this.transportation = 'auto';
    }
  }
};
</script>

<style scoped>
.planning-form {
  /* 你的樣式 */
}
</style>
