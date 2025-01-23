import { defineStore } from 'pinia';

export const usePlaceStore = defineStore('place', {
  state: () => ({
    selectedPlace: null,
  }),
  actions: {
    setSelectedPlace(place) {
      this.selectedPlace = place;
    },
  },
});