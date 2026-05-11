import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';
const client = axios.create({
    baseURL: '/api',
    timeout: 10000,
});
client.interceptors.request.use((config) => {
    const authStore = useAuthStore();
    if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
});
client.interceptors.response.use((response) => {
    const payload = response.data;
    if (payload.code !== 0) {
        throw new Error(payload.message || '请求失败');
    }
    return response;
}, (error) => {
    const message = error.response?.data?.message || error.message || '网络请求失败';
    ElMessage.error(message);
    if (error.response?.status === 401) {
        const authStore = useAuthStore();
        authStore.logout();
    }
    return Promise.reject(error);
});
const http = {
    async get(url, config = {}) {
        const response = await client.get(url, config);
        return response.data.data;
    },
    async post(url, data, config = {}) {
        const response = await client.post(url, data, config);
        return response.data.data;
    },
    async put(url, data, config = {}) {
        const response = await client.put(url, data, config);
        return response.data.data;
    },
    async delete(url, config = {}) {
        const response = await client.delete(url, config);
        return response.data.data;
    },
};
export default http;
