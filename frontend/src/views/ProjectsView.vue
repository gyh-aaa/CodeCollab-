<script setup lang="ts">
import { Plus, Refresh, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { createProjectApi, listOrganizationsApi, listProjectsApi } from '@/api/project'
import type { Organization, ProjectSummary } from '@/types/project'

const router = useRouter()
const loading = ref(false)
const createVisible = ref(false)
const selectedOrganizationId = ref<number>()
const organizations = ref<Organization[]>([])
const projects = ref<ProjectSummary[]>([])

const manageableOrganizations = computed(() => {
  return organizations.value.filter((item) => item.currentUserRole === 'OWNER' || item.currentUserRole === 'ADMIN')
})

const createForm = reactive({
  organizationId: undefined as number | undefined,
  name: '',
  code: '',
  description: '',
  dateRange: [] as string[],
})

async function loadData() {
  loading.value = true
  try {
    const [organizationList, projectList] = await Promise.all([
      listOrganizationsApi(),
      listProjectsApi(selectedOrganizationId.value),
    ])
    organizations.value = organizationList
    projects.value = projectList
    if (!createForm.organizationId && manageableOrganizations.value.length) {
      createForm.organizationId = manageableOrganizations.value[0].id
    }
  } finally {
    loading.value = false
  }
}

function progress(project: ProjectSummary) {
  if (!project.totalTasks) {
    return 0
  }
  return Math.round((project.completedTasks / project.totalTasks) * 100)
}

function statusType(status: ProjectSummary['status']) {
  if (status === 'ACTIVE') {
    return 'success'
  }
  if (status === 'ARCHIVED') {
    return 'info'
  }
  return 'warning'
}

async function submitProject() {
  if (!createForm.organizationId || !createForm.name.trim() || !createForm.code.trim()) {
    ElMessage.warning('请选择组织，并填写项目名称和编码')
    return
  }
  await createProjectApi({
    organizationId: createForm.organizationId,
    name: createForm.name.trim(),
    code: createForm.code.trim().toUpperCase(),
    description: createForm.description.trim() || undefined,
    startDate: createForm.dateRange?.[0],
    endDate: createForm.dateRange?.[1],
  })
  ElMessage.success('项目已创建')
  createVisible.value = false
  createForm.name = ''
  createForm.code = ''
  createForm.description = ''
  createForm.dateRange = []
  await loadData()
}

function openDetail(project: ProjectSummary) {
  router.push({ name: 'project-detail', params: { id: project.id } })
}

onMounted(loadData)
</script>

<template>
  <div class="page-stack">
    <section class="toolbar-row">
      <div>
        <h2>项目列表</h2>
        <p>管理组织内的项目、成员和交付进度。</p>
      </div>
      <div class="toolbar-actions">
        <el-select v-model="selectedOrganizationId" clearable placeholder="全部组织" class="toolbar-select" @change="loadData">
          <el-option v-for="organization in organizations" :key="organization.id" :label="organization.name" :value="organization.id" />
        </el-select>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
        <el-button type="primary" :icon="Plus" :disabled="!manageableOrganizations.length" @click="createVisible = true">
          新建项目
        </el-button>
      </div>
    </section>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="projects">
        <el-table-column prop="name" label="项目名称" min-width="180">
          <template #default="{ row }">
            <div class="table-title">
              <strong>{{ row.name }}</strong>
              <span>{{ row.description || row.organizationName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="organizationName" label="组织" min-width="160" />
        <el-table-column prop="code" label="编码" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="140" />
        <el-table-column prop="currentUserRole" label="我的角色" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="任务进度" min-width="220">
          <template #default="{ row }">
            <el-progress :percentage="progress(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" :icon="View" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createVisible" title="新建项目" width="560px">
      <el-form label-position="top">
        <el-form-item label="所属组织">
          <el-select v-model="createForm.organizationId" placeholder="选择组织">
            <el-option v-for="organization in manageableOrganizations" :key="organization.id" :label="organization.name" :value="organization.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="createForm.name" maxlength="128" placeholder="协作平台三期" />
        </el-form-item>
        <el-form-item label="项目编码">
          <el-input v-model="createForm.code" maxlength="32" placeholder="CCP3" />
        </el-form-item>
        <el-form-item label="项目周期">
          <el-date-picker
            v-model="createForm.dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="createForm.description" type="textarea" :rows="3" maxlength="512" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProject">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>
