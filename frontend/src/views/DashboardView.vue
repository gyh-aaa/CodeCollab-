<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import VChart from 'vue-echarts'
import { BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'

import { listProjectsApi } from '@/api/project'
import type { ProjectSummary } from '@/types/project'

use([CanvasRenderer, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

const loading = ref(false)
const projects = ref<ProjectSummary[]>([])

const totalTasks = computed(() => projects.value.reduce((sum, item) => sum + item.totalTasks, 0))
const completedTasks = computed(() => projects.value.reduce((sum, item) => sum + item.completedTasks, 0))
const completionRate = computed(() => {
  if (!totalTasks.value) {
    return 0
  }
  return Math.round((completedTasks.value / totalTasks.value) * 100)
})

const barOption = computed(() => ({
  tooltip: {},
  grid: { left: 32, right: 16, top: 24, bottom: 24 },
  xAxis: { type: 'category', data: projects.value.map((item) => item.code) },
  yAxis: { type: 'value' },
  series: [
    {
      name: '任务数',
      type: 'bar',
      data: projects.value.map((item) => item.totalTasks),
      itemStyle: { color: '#2563eb' },
      barWidth: 28,
    },
  ],
}))

const pieOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [
    {
      name: '完成情况',
      type: 'pie',
      radius: ['46%', '68%'],
      data: [
        { value: completedTasks.value, name: '已完成', itemStyle: { color: '#16a34a' } },
        { value: totalTasks.value - completedTasks.value, name: '未完成', itemStyle: { color: '#f59e0b' } },
      ],
    },
  ],
}))

async function loadProjects() {
  loading.value = true
  try {
    projects.value = await listProjectsApi()
  } finally {
    loading.value = false
  }
}

onMounted(loadProjects)
</script>

<template>
  <div v-loading="loading" class="page-stack">
    <section class="metric-grid">
      <el-card shadow="never" class="metric-card">
        <span>活跃项目</span>
        <strong>{{ projects.length }}</strong>
      </el-card>
      <el-card shadow="never" class="metric-card">
        <span>任务总数</span>
        <strong>{{ totalTasks }}</strong>
      </el-card>
      <el-card shadow="never" class="metric-card">
        <span>已完成</span>
        <strong>{{ completedTasks }}</strong>
      </el-card>
      <el-card shadow="never" class="metric-card">
        <span>完成率</span>
        <strong>{{ completionRate }}%</strong>
      </el-card>
    </section>

    <section class="analytics-grid">
      <el-card shadow="never">
        <template #header>项目任务分布</template>
        <VChart class="chart" :option="barOption" autoresize />
      </el-card>

      <el-card shadow="never">
        <template #header>整体完成情况</template>
        <VChart class="chart" :option="pieOption" autoresize />
      </el-card>
    </section>

    <el-card shadow="never">
      <template #header>近期项目</template>
      <el-table :data="projects">
        <el-table-column prop="name" label="项目名称" min-width="180" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="140" />
        <el-table-column label="进度" min-width="180">
          <template #default="{ row }">
            <el-progress :percentage="Math.round((row.completedTasks / row.totalTasks) * 100)" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
