// src/services/api.js
import axios from 'axios';

const apiUrl = 'http://192.168.23.77:8080/api/data';

export const getData = () => axios.get(apiUrl);
export const addItem = (item) => axios.post(apiUrl, item);
export const deleteItem = (id) => axios.delete(`${apiUrl}/${id}`);
export const updateItem = (id, item) => axios.put(`${apiUrl}/${id}`, item);
