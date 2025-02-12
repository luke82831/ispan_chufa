import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSearchStore = defineStore('search', () => {
  const searchTitle = ref('');
  const isSearch = ref(false); // 是否正在搜索
  const searchResults = ref([]);
  
  const setSearchTitle = (title) => {
    searchTitle.value = title;
  };

  const setSearchResults = (results) => {
    searchResults.value = results;
  };

  const resetSearch = () => {
    searchTitle.value = '';
    isSearch.value = false;
    searchResults.value = [];
  };

  return {
    searchTitle,
    isSearch,
    searchResults,
    setSearchTitle,
    setSearchResults,
    resetSearch,
  };
});
