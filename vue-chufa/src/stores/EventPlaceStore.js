// EventPlaceStore.js (Option API)
import { defineStore } from "pinia";
import axiosapi from "@/plugins/axios";

export const useEventPlaceStore = defineStore("eventPlaceStore", {
    state: () => ({
        eventPlaceList: [],
    }),

    actions: {
        async addPlaceToEvent(eventId, placeId) {
            try {
                // 假設後端提供 POST /api/event-place
                const response = await axiosapi.post("/api/event-place", {
                    eventId,
                    placeId,
                });
                const newRelation = response.data;
                this.eventPlaceList.push(newRelation);
                return newRelation;
            } catch (error) {
                console.error("[addPlaceToEvent] 發生錯誤:", error);
                throw error;
            }
        },
    },
});
