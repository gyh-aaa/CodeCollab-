<script setup lang="ts">
import { Filter, Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { changeTaskStatusApi, getProjectBoardApi, listProjectsApi } from '@/api/project'
import type { BoardColumn, ProjectSummary, TaskCard } from '@/types/project'

const router = useRouter()
const loading = ref(false)
const projects = ref<ProjectSummary[]>([])
const selectedProjectId = ref<number>()
const columns = ref<BoardColumn[]>([])

const currentProject = computed(() => projects.value.find((item) => item.id === selectedProjectId.value))

const priorityMap: Record<TaskCard['priority'], 'danger' | 'warning' | 'info'> = {
  HIGH: 'danger',
  MEDIUM: 'warning',
  LOW: 'info',
}

async function loadProjects() {
  projects.value = await listProjectsApi()
  if (!selectedProjectId.value && projects.value.length) {
    selectedProjectId.value = projects.value[0].id
  }
}

async function loadBoard() {
  if (!selectedProjectId.value) {
    columns.value = []
    return
  }
  loading.value = true
  try {
    columns.value = await getProjectBoardApi(selectedProjectId.value)
  } finally {
    loading.value = false
  }
}

async function loadData() {
  await loadProjects()
  await loadBoard()
}

async function moveTask(task: TaskCard, columnKey: string) {
  const nextStatus = columnKey === 'TODO' ? 'IN_PROGRESS' : columnKey === 'IN_PROGRESS' ? 'DONE' : 'TODO'
  await changeTaskStatusApi(task.id, nextStatus)
  ElMessage.success(`任务已流转到 ${nextStatus}`)
  await loadBoard()
}

function openProjectDetail() {
  if (selectedProjectId.value) {
    router.push({ name: 'project-detail', params: { id: selectedProjectId.value } })
  }
}

onMounted(loadData)
</script>

<template>
  <div v-loading="loading" class="page-stack">
    <section class="toolbar-row">
      <div>
        <h2>任务看板</h2>
        <p>{{ currentProject ? `${currentProject.name} · ${currentProject.code}` : '选择项目后查看任务状态分布' }}</p>
      </div>
      <div class="toolbar-actions">
        <el-select v-model="selectedProjectId" placeholder="选择项目" class="toolbar-select" @change="loadBoard">
          <el-option v-for="project in projects" :key="project.id" :label="project.name" :value="project.id" />
        </el-select>
        <el-button :icon="Filter">筛选</el-button>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
        <el-button type="primary" :icon="Plus" :disabled="!selectedProjectId" @click="openProjectDetail">新建任务</el-button>
      </div>
    </section>

    <section class="board-grid">
      <div v-for="column in columns" :key="column.key" class="board-column">
        <header>
          <strong>{{ column.title }}</strong>
          <el-tag round>{{ column.tasks.length }}</el-tag>
        </header>

        <article v-for="task in column.tasks" :key="task.id" class="task-card">
          <div class="task-title">{{ task.title }}</div>
          <div class="task-meta">
            <el-tag :type="priorityMap[task.priority]" size="small">{{ task.priority }}</el-tag>
            <span>{{ task.assigneeName }}</span>
          </div>
          <div class="task-footer">
            <span>{{ task.dueDate }}</span>
            <div>
              <el-tag v-for="label in task.labels" :key="label" size="small" effect="plain">
                {{ label }}
              </el-tag>
            </div>
          </div>
          <div class="task-actions">
            <el-button text type="primary" @click="moveTask(task, column.key)">流转</el-button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>
