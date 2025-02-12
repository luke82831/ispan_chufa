import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSearchStore = defineStore('search', () => {
  const searchTitle = ref('');
  const isSearch = ref(false); // 是否正在搜索
  const searchResults = ref([]);
  const searchUsers = ref([]);   // 搜索的用户
  //const selectedPlace = ref(null); // 默认选中的标签
  const selectedTab =ref(null);
  
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
    //selectedPlace.value=null;
    selectedTab.value=null;
    //console.log(selectedPlace);
  };

  
  // const setselectedPlace = (tab) => {
  //   selectedPlace.value = tab;
  // };
  const setselectedTab=(tab) => {
    selectedTab.value = tab;
  };

  return {
    searchTitle,
    isSearch,
    searchResults,
    setSearchTitle,
    setSearchResults,
    resetSearch,
    searchUsers,
    //selectedPlace,
    //setselectedPlace,
    setselectedTab,
  };
});
