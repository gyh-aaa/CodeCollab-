export interface CurrentUser {
  id: number
  username: string
  nickname: string
  roles: string[]
  permissions: string[]
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: CurrentUser
}
