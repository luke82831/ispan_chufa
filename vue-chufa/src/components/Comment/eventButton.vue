<template>
    <button :class=EventButtonClass @click="showEvent">{{ event.date }}</button>
</template>
    
<script setup>
    import { ref, onMounted } from 'vue';
    import eventBus from '@/eventBus';
    const props = defineProps(['event','hasEvent'])
    const EventButtonClass = ref("openEventButton")
    onMounted(()=>{
        eventBus.on('EventButtonRemove',()=>{
            EventButtonClass.value = "openEventButton"
        })
    })
    const showEvent = () => {
        eventBus.emit('EventButtonRemove');
        EventButtonClass.value ='inEventButton'
        props.hasEvent.shift();
        props.hasEvent.shift();
        props.hasEvent.push(props.event.eventXPlaceBeans);
        props.hasEvent.push(props.event.startTime);
    }
</script>
    
<style scoped>
    .openEventButton{
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
    .openEventButton:hover {
        transform: scale(1.05);
    }
    .inEventButton{
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
    .inEventButton:hover {
        transform: scale(1.05);
    }
</style>