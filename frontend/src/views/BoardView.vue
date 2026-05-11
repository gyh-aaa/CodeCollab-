<script setup lang="ts">
import { Filter, Plus, Refresh } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'

import { getProjectBoardApi } from '@/api/project'
import type { BoardColumn, TaskCard } from '@/types/project'

const loading = ref(false)
const columns = ref<BoardColumn[]>([])

const priorityMap: Record<TaskCard['priority'], 'danger' | 'warning' | 'info'> = {
  HIGH: 'danger',
  MEDIUM: 'warning',
  LOW: 'info',
}

async function loadBoard() {
  loading.value = true
  try {
    columns.value = await getProjectBoardApi(1)
  } finally {
    loading.value = false
  }
}

onMounted(loadBoard)
</script>

<template>
  <div v-loading="loading" class="page-stack">
    <section class="toolbar-row">
      <div>
        <h2>任务看板</h2>
        <p>按任务状态推进项目交付，拖拽排序会在后续阶段接入。</p>
      </div>
      <div class="toolbar-actions">
        <el-button :icon="Filter">筛选</el-button>
        <el-button :icon="Refresh" @click="loadBoard">刷新</el-button>
        <el-button type="primary" :icon="Plus">新建任务</el-button>
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
        </article>
      </div>
    </section>
  </div>
</template>
