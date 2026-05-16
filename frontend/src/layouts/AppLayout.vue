<script setup lang="ts">
import {
  Bell,
  DataBoard,
  Folder,
  Grid,
  House,
  OfficeBuilding,
  SwitchButton,
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'
import type { MenuItem } from '@/types/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => String(route.meta.title || '工作台'))
const fallbackMenus: MenuItem[] = [
  { key: 'dashboard', title: '工作台', path: '/dashboard', icon: 'House', permission: 'dashboard:view' },
  { key: 'organizations', title: '组织', path: '/organizations', icon: 'OfficeBuilding', permission: 'organization:read' },
  { key: 'projects', title: '项目', path: '/projects', icon: 'Folder', permission: 'project:read' },
  { key: 'board', title: '任务看板', path: '/board', icon: 'Grid', permission: 'task:read' },
  { key: 'notifications', title: '通知', path: '/notifications', icon: 'Bell', permission: 'notification:read' },
]

const iconMap = {
  Bell,
  Folder,
  Grid,
  House,
  OfficeBuilding,
}

const menus = computed(() => {
  const source = authStore.user?.menus?.length ? authStore.user.menus : fallbackMenus
  if (source.some((item) => item.key === 'organizations')) {
    return source
  }
  const organizationsMenu = fallbackMenus.find((item) => item.key === 'organizations')!
  const insertIndex = source.findIndex((item) => item.key === 'projects')
  if (insertIndex === -1) {
    return [...source, organizationsMenu]
  }
  return [...source.slice(0, insertIndex), organizationsMenu, ...source.slice(insertIndex)]
})

function menuIcon(icon: string) {
  return iconMap[icon as keyof typeof iconMap] || House
}

function handleSelect(path: string) {
  router.push(path)
}

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside width="248px" class="app-sidebar">
      <div class="brand">
        <div class="brand-mark">C</div>
        <div>
          <strong>CodeCollab</strong>
          <span>项目协作系统</span>
        </div>
      </div>

      <el-menu :default-active="activeMenu" class="side-menu" @select="handleSelect">
        <el-menu-item v-for="item in menus" :key="item.key" :index="item.path">
          <el-icon>
            <component :is="menuIcon(item.icon)" />
          </el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div>
          <p class="eyebrow">Project Workspace</p>
          <h1>{{ pageTitle }}</h1>
        </div>

        <div class="header-actions">
          <el-button :icon="DataBoard" text>统计</el-button>
          <el-dropdown>
            <el-button>
              {{ authStore.user?.nickname || '未登录' }}
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>{{ authStore.user?.username }}</el-dropdown-item>
                <el-dropdown-item :icon="SwitchButton" @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>
