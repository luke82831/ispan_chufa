<template>
    <div>
    <h1>Google Maps 地點搜尋</h1>
    <input
        type="text"
        id="search-input"
        placeholder="搜尋地點"
    />
    <div id="map" style="height: 800px; width: 100%;"></div>
    <button @click="saveSelectedPlace">儲存地點</button>
    </div>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return {
            map: null,
            autocomplete: null,
            selectedPlace: null,
            marker: null,
        };
    },
    methods: {
        // 初始化 Google Map
        initMap() {
            // 動態導入 Google Maps API 的 marker 函式庫
            const { AdvancedMarkerElement, PinElement } = google.maps.importLibrary("marker");

            // 顯示預設地址
            const mapOptions = {
                center: { lat: 25.0339643, lng: 121.564468 },
                zoom: 17,
                mapId: 'DEMO_MAP_ID',
            };

            // 產生地圖
            this.map = new google.maps.Map(document.getElementById('map'), mapOptions);

            // this.marker = new google.maps.Marker({
            //         map: this.map,
            //         position: { lat: 25.0339643, lng: 121.564468 },
            //         title: "預設地點", // 可選：標記的提示文字
            //     });

            // 創建初始標記Marker
            if (google.maps.marker && google.maps.marker.AdvancedMarkerElement) {
            const marker = new google.maps.marker.AdvancedMarkerElement({
                map: this.map,
                position: { lat: 25.0339643, lng: 121.564468 },
                title: "預設地點", // 可選：標記的提示文字
            });
            } else {
                console.error("AdvancedMarkerElement is not available in this version of Google Maps API.");
            }

            // 設定自動完成搜尋框
            const input = document.getElementById("search-input");
            this.autocomplete = new google.maps.places.Autocomplete(input);
            
            this.autocomplete.addListener("place_changed", () => {
                this.selectedPlace = this.autocomplete.getPlace();
                if (this.selectedPlace && this.selectedPlace.geometry) {
                    this.updateMarker(); // 搜尋地點後更新標記
                    // this.map.setCenter(this.selectedPlace.geometry.location);
                    // this.map.setZoom(15);
                } else {
                    console.warn("未找到有效的地點");
                }
            });
        },
        updateMarker() {
            const location = this.selectedPlace.geometry.location;
            // 更新標記位置
            this.marker.setPosition(location);
            // 更新地圖中心點
            this.map.setCenter(location);
            this.map.setZoom(15);
            // 可選：更新圖標
            this.marker.setIcon({
                url: "https://example.com/custom-icon.png", // 自訂圖標 URL
                scaledSize: new google.maps.Size(50, 50),   // 設置圖標大小
            });
            console.log(`標記更新到位置：${location.lat()}, ${location.lng()}`);
        },
        
        // 保存選擇的地點
        saveSelectedPlace() {
            if (!this.selectedPlace) {
                alert("請先選擇一個地點！");
                return;
            }

            const placeData = {
                name: this.selectedPlace.name,
                address: this.selectedPlace.formatted_address || "無地址資訊",
                latitude: this.selectedPlace.geometry.location.lat(),
                longitude: this.selectedPlace.geometry.location.lng(),
            };

            axios
                .post("http://localhost:8080/api/savePlace", placeData)
                .then((response) => {
                    alert("地點已成功儲存！");
                    console.log(response.data);
                })
                .catch((error) => {
                    alert("儲存地點失敗！");
                    console.error(error);
                });
        },
    },
    mounted() {
        const script = document.createElement("script");
        script.src = `https://maps.googleapis.com/maps/api/js?key=AIzaSyDnE2Py4Lf614tVdpqiAsanfttAfJw5FbM&callback=initMap&v=weekly&libraries=marker`;
        script.async = true;
        script.defer = true;
        window.initMap = this.initMap;
        document.head.appendChild(script);
        // 檢查 Google Maps API 是否加載完成
        // if (window.google && window.google.maps) {
        //     this.initMap();
        // } else {
        //     console.error("Google Maps API 未加载成功");
        // }
    },
}
</script>

<style>
#map {
    margin-top: 20px;
    margin-bottom: 20px;
}
</style>