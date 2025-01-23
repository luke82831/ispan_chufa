<template>
    <div>
        <p class="title">{{ title }}</p>
        <p v-show="title!=null">==================================</p>
        <div class="container">
            <div id="container"></div>
        </div>
    </div>
</template>
    
<script>
    import axiosapi from '@/plugins/axios.js';
    export default {
        data() {
            return {
                title: null,
            };
        },
        methods: {
            async findPost() {
                const response = await axiosapi.get(`/post/findById/${this.$route.params.postid}`);
                console.log(response.data.message);
                if (response.data.successs) {
                    this.title=response.data.list[0].postTitle
                    container.innerHTML = response.data.list[0].postContent
                }else{
                    alert(response.data.message);
                }
            }
        },
        mounted() {
            this.findPost(); // 在元件掛載後執行
        }
    };
</script>
    
<style>
    .title {
        font-size: 50px;
        margin: 20px;
    }
</style>