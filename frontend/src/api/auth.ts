import http from './http'

import type { CurrentUser, LoginResponse } from '@/types/auth'

export interface LoginPayload {
  username: string
  password: string
}

export function loginApi(payload: LoginPayload) {
  return http.post<LoginResponse>('/auth/login', payload)
}

export function currentUserApi() {
  return http.get<CurrentUser>('/auth/me')
}
