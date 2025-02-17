<template>
    <div v-if="placeData!=null" class="placeBox">
        <h2>地點名稱:{{ placeData.placeName }}</h2>
        <div>
            <div>
                <button :class="clickImg(index)" v-for="(photo,index) in placeData.photos" @click="openPhoto(photo,index)">圖片{{index+1}}</button>
                <button v-if="photoSrc!=null" class="ccc" @click="openPhoto(null)">關閉圖片</button>
            </div>
            <img class="placeImg" v-if="photoSrc!=null" :src=photoSrc alt="">  
        </div>
        <button @click="openPlaceData = !openPlaceData">詳細資訊</button>
        <div v-if="openPlaceData">
            <h2>地址:{{ placeData.placeAddress }}</h2>
            <h2>地點電話:{{ placeData.placePhone }}</h2>
            <h2>營業時間:{{ placeData.businessHours }}</h2>
            <h2>評價:{{ placeData.rating }}顆星</h2>
            <h2>網站:<a :href="placeData.website">{{ placeData.website }}</a></h2>
            <h2>Goole map:<a :href="placeData.bookingUrl">{{ placeData.bookingUrl }}</a></h2>
            <h2 v-if="placeData.priceLevel!=null">價格水平:{{ placeData.priceLevel }}</h2>
            <h2 v-if="placeData.accommodationType!=null">住宿類型:{{ placeData.accommodationType }}</h2>
        </div>
    </div>
</template>
    
<script setup>
    import { onMounted, ref, watch } from 'vue';
    import axiosapi from "@/plugins/axios.js";
    import eventBus from '@/eventBus';
    const props = defineProps(['placeId'])
    const photoSrc = ref(null)
    const photoSrcIndex = ref(null)
    const openPlaceData = ref(false)
    const openPhoto = (photo,index) => {
        photoSrc.value = photo
        photoSrcIndex.value = index
    }
    watch(
        ()=>props.placeId,
        ()=>findPlacet()
    )

    onMounted(()=>{
        findPlacet()
    })
    const placeData = ref(null)

    const clickImg = (index) => {
        if(photoSrcIndex.value==index){
            return "bbb"
        }
        return "aaa"
    }
    const findPlacet = async ()=>{
        console.log("搜尋Place資料")
        const response = await axiosapi.get(`/api/place/${props.placeId}`);
        console.log(response)
        placeData.value = response.data
        photoSrc.value = placeData.value.photos[0]
        photoSrcIndex.value = 0
    }
</script>
    
<style scoped>
    .placeImg{
        width: 400px;
    }
    .placeBox{
        display: flex; /* 啟用Flexbox佈局 */
        flex-direction: column;
        justify-content: space-around;
        padding: 25px;
        margin: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
    }
    .aaa{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #17a2b8;
        color: white;
        margin-bottom: 10px; 
        margin-top: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .aaa:hover {
        transform: scale(1.05);
    }
    .bbb{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #1757b8;
        color: white;
        margin-bottom: 10px; 
        margin-top: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .bbb:hover {
        transform: scale(1.05);
    }
    .ccc{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #b83d17;
        color: white;
        margin-bottom: 10px; 
        margin-top: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .ccc:hover {
        transform: scale(1.05);
    }
</style>