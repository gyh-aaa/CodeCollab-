export interface MenuItem {
  key: string
  title: string
  path: string
  icon: string
  permission: string
}

export interface CurrentUser {
  id: number
  username: string
  nickname: string
  roles: string[]
  permissions: string[]
  menus: MenuItem[]
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: CurrentUser
}
