<template>
    <div v-if="placeData!=null">
        <h2>地點名稱:{{ placeData.placeName }}</h2>
        <h2>地點區域:{{ placeData.city }}{{ placeData.region }}</h2>
        <h2>地址:{{ placeData.placeAddress }}</h2>
        <div>
            <div>
                <button v-for="(photo,index) in placeData.photos" @click="openPhoto(photo)">圖片{{index+1}}</button>
                <button @click="openPhoto(null)">關閉圖片</button>
            </div>
            <img class="placeImg" v-if="photoSrc!=null" :src=photoSrc alt="">  
        </div>
        <h2>地點電話:{{ placeData.placePhone }}</h2>
        <h2>營業時間:{{ placeData.businessHours }}</h2>
        <h2>評價:{{ placeData.rating }}顆星</h2>
        <h2>網站:<a :href="placeData.website">{{ placeData.website }}</a></h2>
        <h2>Goole map:<a :href="placeData.bookingUrl">{{ placeData.bookingUrl }}</a></h2>
        <h2 v-if="placeData.priceLevel!=null">價格水平:{{ placeData.priceLevel }}</h2>
        <h2 v-if="placeData.accommodationType!=null">住宿類型:{{ placeData.accommodationType }}</h2>
        <h2>預訂:{{ placeData.reservation }}</h2>
    </div>
</template>
    
<script setup>
    import { onMounted, ref, watch } from 'vue';
    import axiosapi from "@/plugins/axios.js";
    import eventBus from '@/eventBus';
    const props = defineProps(['placeId'])
    const photoSrc = ref(null)
    const openPhoto = (photo) => {
        console.log(photo)
        photoSrc.value = photo
    }
    watch(
        ()=>props.placeId,
        ()=>findPlacet()
    )
    onMounted(()=>{
        findPlacet()
    })
    const placeData = ref(null)
    const findPlacet = async ()=>{
        console.log("搜尋Place資料")
        const response = await axiosapi.get(`/api/place/${props.placeId}`);
        console.log(response)
        placeData.value = response.data
    }
</script>
    
<style scoped>
    .placeImg{
        width: 400px;
    }

</style>