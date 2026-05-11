<script setup lang="ts">
import { Plus, Refresh } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'

import { listProjectsApi } from '@/api/project'
import type { ProjectSummary } from '@/types/project'

const loading = ref(false)
const projects = ref<ProjectSummary[]>([])

async function loadProjects() {
  loading.value = true
  try {
    projects.value = await listProjectsApi()
  } finally {
    loading.value = false
  }
}

function progress(project: ProjectSummary) {
  return Math.round((project.completedTasks / project.totalTasks) * 100)
}

onMounted(loadProjects)
</script>

<template>
  <div class="page-stack">
    <section class="toolbar-row">
      <div>
        <h2>项目列表</h2>
        <p>管理组织内的项目、成员和交付进度。</p>
      </div>
      <div class="toolbar-actions">
        <el-button :icon="Refresh" @click="loadProjects">刷新</el-button>
        <el-button type="primary" :icon="Plus">新建项目</el-button>
      </div>
    </section>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="projects">
        <el-table-column prop="name" label="项目名称" min-width="180" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="140" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="任务进度" min-width="220">
          <template #default="{ row }">
            <el-progress :percentage="progress(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <el-button text type="primary">详情</el-button>
          <el-button text>成员</el-button>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
