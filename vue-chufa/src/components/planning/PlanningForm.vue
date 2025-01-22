<template>
  <form @submit.prevent="handleSubmit">
    <div>
      <label for="itinerary-name">行程名稱：</label>
      <input type="text" id="itinerary-name" v-model="formData.name" />
    </div>
    <div>
      <label for="start-date">開始日期：</label>
      <input type="date" id="start-date" v-model="formData.startDate" />
    </div>
    <div>
      <label for="end-date">結束日期：</label>
      <input type="date" id="end-date" v-model="formData.endDate" />
    </div>
    <div>
      <label for="purpose">目的：</label>
      <textarea id="purpose" v-model="formData.purpose"></textarea>
    </div>
    <button type="submit">建立行程</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      formData: {
        name: "",
        startDate: "",
        endDate: "",
        purpose: "",
      },
    };
  },
  methods: {
    handleSubmit() {
      console.log("行程資料：", this.formData);
      // 保存資料（如果需要）
      localStorage.setItem("itineraryData", JSON.stringify(this.formData));

      // 導航到 Map.vue
      this.$router.push("/planningtabs");
    },
  },
};
</script>

<style></style>
