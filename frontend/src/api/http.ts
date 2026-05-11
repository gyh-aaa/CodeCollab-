import axios from 'axios'
import { ElMessage } from 'element-plus'

import { useAuthStore } from '@/stores/auth'

export interface ApiEnvelope<T> {
  code: number
  message: string
  data: T
  traceId?: string
}

const client = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

client.interceptors.request.use((config) => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiEnvelope<unknown>
    if (payload.code !== 0) {
      throw new Error(payload.message || '请求失败')
    }
    return response
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络请求失败'
    ElMessage.error(message)
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
    }
    return Promise.reject(error)
  },
)

const http = {
  async get<T>(url: string, config = {}) {
    const response = await client.get<ApiEnvelope<T>>(url, config)
    return response.data.data
  },
  async post<T>(url: string, data?: unknown, config = {}) {
    const response = await client.post<ApiEnvelope<T>>(url, data, config)
    return response.data.data
  },
  async put<T>(url: string, data?: unknown, config = {}) {
    const response = await client.put<ApiEnvelope<T>>(url, data, config)
    return response.data.data
  },
  async delete<T>(url: string, config = {}) {
    const response = await client.delete<ApiEnvelope<T>>(url, config)
    return response.data.data
  },
}

export default http
