<script setup lang="ts">
import { ArrowLeft, Box, Delete, Edit, Plus, Refresh, UserFilled, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  addProjectMemberApi,
  archiveProjectApi,
  changeTaskStatusApi,
  createTaskApi,
  deleteTaskApi,
  getProjectApi,
  listProjectMembersApi,
  listTaskLabelsApi,
  listTaskLogsApi,
  listTasksApi,
  updateProjectApi,
  updateTaskApi,
} from '@/api/project'
import type { Member, ProjectDetail, ProjectRole, Task, TaskLabel, TaskLog } from '@/types/project'

const route = useRoute()
const router = useRouter()
const projectId = computed(() => Number(route.params.id))
const loading = ref(false)
const memberLoading = ref(false)
const taskLoading = ref(false)
const editVisible = ref(false)
const taskVisible = ref(false)
const taskLogVisible = ref(false)
const project = ref<ProjectDetail>()
const members = ref<Member[]>([])
const tasks = ref<Task[]>([])
const labels = ref<TaskLabel[]>([])
const taskLogs = ref<TaskLog[]>([])
const editingTask = ref<Task | null>(null)

const editForm = reactive({
  name: '',
  description: '',
  dateRange: [] as string[],
})

const memberForm = reactive({
  username: '',
  projectRole: 'DEVELOPER' as ProjectRole,
})

const taskForm = reactive({
  title: '',
  description: '',
  status: 'TODO' as Task['status'],
  priority: 'MEDIUM' as Task['priority'],
  assigneeId: undefined as number | undefined,
  dueDate: '',
  labelIds: [] as number[],
})

const canManageProject = computed(() => {
  return project.value?.currentUserRole === 'OWNER' || project.value?.currentUserRole === 'MANAGER'
})

const completionRate = computed(() => {
  if (!project.value?.totalTasks) {
    return 0
  }
  return Math.round((project.value.completedTasks / project.value.totalTasks) * 100)
})

async function loadProject() {
  loading.value = true
  try {
    project.value = await getProjectApi(projectId.value)
    editForm.name = project.value.name
    editForm.description = project.value.description || ''
    editForm.dateRange = project.value.startDate && project.value.endDate ? [project.value.startDate, project.value.endDate] : []
  } finally {
    loading.value = false
  }
}

async function loadMembers() {
  memberLoading.value = true
  try {
    members.value = await listProjectMembersApi(projectId.value)
  } finally {
    memberLoading.value = false
  }
}

async function loadTasks() {
  taskLoading.value = true
  try {
    tasks.value = await listTasksApi(projectId.value)
    labels.value = await listTaskLabelsApi(projectId.value)
  } finally {
    taskLoading.value = false
  }
}

async function submitProject() {
  if (!project.value || !editForm.name.trim()) {
    ElMessage.warning('请输入项目名称')
    return
  }
  project.value = await updateProjectApi(project.value.id, {
    name: editForm.name.trim(),
    description: editForm.description.trim() || undefined,
    startDate: editForm.dateRange?.[0],
    endDate: editForm.dateRange?.[1],
  })
  ElMessage.success('项目已更新')
  editVisible.value = false
}

async function submitMember() {
  if (!project.value || !memberForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  await addProjectMemberApi(project.value.id, {
    username: memberForm.username.trim(),
    projectRole: memberForm.projectRole,
  })
  ElMessage.success('成员已保存')
  memberForm.username = ''
  memberForm.projectRole = 'DEVELOPER'
  await Promise.all([loadMembers(), loadProject()])
}

function resetTaskForm() {
  editingTask.value = null
  taskForm.title = ''
  taskForm.description = ''
  taskForm.status = 'TODO'
  taskForm.priority = 'MEDIUM'
  taskForm.assigneeId = undefined
  taskForm.dueDate = ''
  taskForm.labelIds = []
}

function openCreateTask() {
  resetTaskForm()
  taskVisible.value = true
}

function openEditTask(task: Task) {
  editingTask.value = task
  taskForm.title = task.title
  taskForm.description = task.description || ''
  taskForm.status = task.status
  taskForm.priority = task.priority
  taskForm.assigneeId = task.assigneeId
  taskForm.dueDate = task.dueDate || ''
  taskForm.labelIds = task.labels.map((label) => label.id)
  taskVisible.value = true
}

async function submitTask() {
  if (!taskForm.title.trim()) {
    ElMessage.warning('请输入任务标题')
    return
  }
  const payload = {
    title: taskForm.title.trim(),
    description: taskForm.description.trim() || undefined,
    priority: taskForm.priority,
    assigneeId: taskForm.assigneeId,
    dueDate: taskForm.dueDate || undefined,
    labelIds: taskForm.labelIds,
  }
  if (editingTask.value) {
    await updateTaskApi(editingTask.value.id, payload)
    ElMessage.success('任务已更新')
  } else {
    await createTaskApi(projectId.value, {
      ...payload,
      status: taskForm.status,
    })
    ElMessage.success('任务已创建')
  }
  taskVisible.value = false
  await Promise.all([loadTasks(), loadProject()])
}

async function moveTask(task: Task) {
  const nextStatus = task.status === 'TODO' ? 'IN_PROGRESS' : task.status === 'IN_PROGRESS' ? 'DONE' : 'TODO'
  await changeTaskStatusApi(task.id, nextStatus)
  ElMessage.success(`任务已流转到 ${nextStatus}`)
  await Promise.all([loadTasks(), loadProject()])
}

async function removeTask(task: Task) {
  await ElMessageBox.confirm('删除后任务会从列表和看板隐藏。', '删除任务', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deleteTaskApi(task.id)
  ElMessage.success('任务已删除')
  await Promise.all([loadTasks(), loadProject()])
}

async function openTaskLogs(task: Task) {
  taskLogs.value = await listTaskLogsApi(task.id)
  taskLogVisible.value = true
}

async function archiveProject() {
  if (!project.value) {
    return
  }
  await ElMessageBox.confirm('归档后项目仍可查看，但不建议继续新增任务。', '归档项目', {
    confirmButtonText: '归档',
    cancelButtonText: '取消',
    type: 'warning',
  })
  project.value = await archiveProjectApi(project.value.id)
  ElMessage.success('项目已归档')
}

function priorityType(priority: Task['priority']) {
  return priority === 'HIGH' ? 'danger' : priority === 'MEDIUM' ? 'warning' : 'info'
}

function formatDateTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 16) : '-'
}

async function loadAll() {
  await Promise.all([loadProject(), loadMembers(), loadTasks()])
}

onMounted(loadAll)
</script>

<template>
  <div v-loading="loading" class="page-stack">
    <section class="toolbar-row">
      <div>
        <el-button text :icon="ArrowLeft" @click="router.push('/projects')">返回项目列表</el-button>
        <h2>{{ project?.name || '项目详情' }}</h2>
        <p>{{ project?.organizationName }} · {{ project?.code }}</p>
      </div>
      <div class="toolbar-actions">
        <el-button :icon="Refresh" @click="loadAll">刷新</el-button>
        <el-button v-if="canManageProject" @click="editVisible = true">编辑</el-button>
        <el-button v-if="canManageProject && project?.status !== 'ARCHIVED'" type="warning" plain @click="archiveProject">归档</el-button>
      </div>
    </section>

    <section class="metric-grid">
      <el-card shadow="never" class="metric-card">
        <span>项目状态</span>
        <strong>{{ project?.status }}</strong>
      </el-card>
      <el-card shadow="never" class="metric-card">
        <span>成员数</span>
        <strong>{{ project?.memberCount || 0 }}</strong>
      </el-card>
      <el-card shadow="never" class="metric-card">
        <span>任务总数</span>
        <strong>{{ project?.totalTasks || 0 }}</strong>
      </el-card>
      <el-card shadow="never" class="metric-card">
        <span>完成率</span>
        <strong>{{ completionRate }}%</strong>
      </el-card>
    </section>

    <section class="detail-grid">
      <el-card shadow="never">
        <template #header>
          <span>项目概览</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="负责人">{{ project?.ownerName }}</el-descriptions-item>
          <el-descriptions-item label="我的角色">{{ project?.currentUserRole }}</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ project?.startDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ project?.endDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ project?.description || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never">
        <template #header>
          <span>交付进度</span>
        </template>
        <div class="progress-panel">
          <el-progress type="dashboard" :percentage="completionRate" />
          <div>
            <h3>{{ project?.completedTasks || 0 }} / {{ project?.totalTasks || 0 }}</h3>
            <p>任务创建、状态流转和日志会实时反映到项目统计。</p>
          </div>
        </div>
      </el-card>
    </section>

    <el-card shadow="never">
      <template #header>
        <div class="table-header">
          <span>任务列表</span>
          <el-button v-if="canManageProject" type="primary" :icon="Plus" @click="openCreateTask">新建任务</el-button>
        </div>
      </template>

      <el-table v-loading="taskLoading" :data="tasks">
        <el-table-column prop="title" label="任务" min-width="220">
          <template #default="{ row }">
            <div class="table-title">
              <strong>{{ row.title }}</strong>
              <span>{{ row.description || '暂无描述' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="130" />
        <el-table-column label="优先级" width="120">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeName" label="负责人" width="140" />
        <el-table-column label="截止时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.dueDate) }}</template>
        </el-table-column>
        <el-table-column label="标签" min-width="160">
          <template #default="{ row }">
            <div class="tag-row">
              <el-tag v-for="label in row.labels" :key="label.id" :color="label.color" effect="dark" size="small">
                {{ label.name }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" :icon="View" @click="openTaskLogs(row)">日志</el-button>
            <el-button text :icon="Refresh" @click="moveTask(row)">流转</el-button>
            <el-button v-if="canManageProject" text :icon="Edit" @click="openEditTask(row)">编辑</el-button>
            <el-button v-if="canManageProject" text type="danger" :icon="Delete" @click="removeTask(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="table-header">
          <span>项目成员</span>
          <el-form v-if="canManageProject" inline>
            <el-form-item>
              <el-input v-model="memberForm.username" placeholder="用户名，如 member" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="memberForm.projectRole" class="role-select">
                <el-option label="负责人" value="MANAGER" />
                <el-option label="研发" value="DEVELOPER" />
                <el-option label="观察者" value="VIEWER" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="UserFilled" @click="submitMember">保存成员</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table v-loading="memberLoading" :data="members">
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="role" label="项目角色" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
      </el-table>
    </el-card>

    <el-dialog v-model="taskVisible" :title="editingTask ? '编辑任务' : '新建任务'" width="620px">
      <el-form label-position="top">
        <el-form-item label="任务标题">
          <el-input v-model="taskForm.title" maxlength="160" placeholder="实现任务 CRUD 接口" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <section class="form-grid">
          <el-form-item label="状态">
            <el-select v-model="taskForm.status" :disabled="Boolean(editingTask)">
              <el-option label="待处理" value="TODO" />
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="DONE" />
            </el-select>
          </el-form-item>
          <el-form-item label="优先级">
            <el-select v-model="taskForm.priority">
              <el-option label="高" value="HIGH" />
              <el-option label="中" value="MEDIUM" />
              <el-option label="低" value="LOW" />
            </el-select>
          </el-form-item>
          <el-form-item label="负责人">
            <el-select v-model="taskForm.assigneeId" clearable placeholder="未指派">
              <el-option v-for="member in members" :key="member.userId" :label="member.nickname" :value="member.userId" />
            </el-select>
          </el-form-item>
          <el-form-item label="截止时间">
            <el-date-picker v-model="taskForm.dueDate" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
          </el-form-item>
        </section>
        <el-form-item label="标签">
          <el-select v-model="taskForm.labelIds" multiple clearable placeholder="选择标签">
            <el-option v-for="label in labels" :key="label.id" :label="label.name" :value="label.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTask">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑项目" width="560px">
      <el-form label-position="top">
        <el-form-item label="项目名称">
          <el-input v-model="editForm.name" maxlength="128" />
        </el-form-item>
        <el-form-item label="项目周期">
          <el-date-picker
            v-model="editForm.dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" maxlength="512" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :icon="Box" @click="submitProject">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="taskLogVisible" title="任务操作日志" size="520px">
      <el-timeline>
        <el-timeline-item v-for="log in taskLogs" :key="log.id" :timestamp="formatDateTime(log.createdAt)">
          <strong>{{ log.action }}</strong>
          <p>{{ log.detail }}</p>
          <span>{{ log.operatorName }}</span>
        </el-timeline-item>
      </el-timeline>
    </el-drawer>
  </div>
</template>
