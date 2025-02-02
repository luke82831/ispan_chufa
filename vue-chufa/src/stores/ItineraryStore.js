import { defineStore } from "pinia";

export const useItineraryStore = defineStore("itinerary", {
    state: () => ({
        itineraryTitle: "",
        startDate: "",
        endDate: "",
        userId: null,
        coverPhoto: null, // 存封面圖片
        itineraryDates: {}, // 每一天的行程資料，鍵是日期，值是行程細節
        selectedDate: "", // 儲存選擇的日期
    }),

    actions: {
        setItinerary(title, start, end) {
            this.itineraryTitle = title;
            this.startDate = start;
            this.endDate = end;
        },

        setSelectedDate(date) {
            this.selectedDate = date;
        },

        addItineraryDate(date) {
            const newDate = new Date(date);
            if (!this.endDate || newDate > this.endDate) {
                this.endDate = newDate;
            }
        },

        // 新增某個日期的行程
        addPlaceToDay(date, place) {
            if (!this.itineraryDates[date]) {
                this.itineraryDates[date] = []; // 如果該日期尚未有行程，先初始化為空陣列
            }
            this.itineraryDates[date].push(place); // 新增行程
        },

        // 取得某個日期的行程
        getItineraryForDay(date) {
            return this.itineraryDates[date] || []; // 如果該日期沒有行程，回傳空陣列
        },
    },
});
