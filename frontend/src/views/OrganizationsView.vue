<script setup lang="ts">
import { Plus, Refresh, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'

import {
  addOrganizationMemberApi,
  createOrganizationApi,
  listOrganizationMembersApi,
  listOrganizationsApi,
} from '@/api/project'
import type { Member, Organization, OrganizationRole } from '@/types/project'

const loading = ref(false)
const memberLoading = ref(false)
const createVisible = ref(false)
const membersVisible = ref(false)
const organizations = ref<Organization[]>([])
const members = ref<Member[]>([])
const selectedOrganization = ref<Organization | null>(null)

const createForm = reactive({
  name: '',
  description: '',
})

const memberForm = reactive({
  username: '',
  memberRole: 'MEMBER' as OrganizationRole,
})

const canManageSelectedOrganization = computed(() => {
  return selectedOrganization.value?.currentUserRole === 'OWNER' || selectedOrganization.value?.currentUserRole === 'ADMIN'
})

async function loadOrganizations() {
  loading.value = true
  try {
    organizations.value = await listOrganizationsApi()
  } finally {
    loading.value = false
  }
}

async function submitOrganization() {
  if (!createForm.name.trim()) {
    ElMessage.warning('请输入组织名称')
    return
  }
  await createOrganizationApi({
    name: createForm.name.trim(),
    description: createForm.description.trim() || undefined,
  })
  ElMessage.success('组织已创建')
  createVisible.value = false
  createForm.name = ''
  createForm.description = ''
  await loadOrganizations()
}

async function openMembers(organization: Organization) {
  selectedOrganization.value = organization
  membersVisible.value = true
  memberLoading.value = true
  try {
    members.value = await listOrganizationMembersApi(organization.id)
  } finally {
    memberLoading.value = false
  }
}

async function submitMember() {
  if (!selectedOrganization.value || !memberForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  await addOrganizationMemberApi(selectedOrganization.value.id, {
    username: memberForm.username.trim(),
    memberRole: memberForm.memberRole,
  })
  ElMessage.success('成员已保存')
  memberForm.username = ''
  memberForm.memberRole = 'MEMBER'
  members.value = await listOrganizationMembersApi(selectedOrganization.value.id)
  await loadOrganizations()
}

onMounted(loadOrganizations)
</script>

<template>
  <div class="page-stack">
    <section class="toolbar-row">
      <div>
        <h2>组织空间</h2>
        <p>组织是项目、成员和业务权限的上层边界。</p>
      </div>
      <div class="toolbar-actions">
        <el-button :icon="Refresh" @click="loadOrganizations">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="createVisible = true">新建组织</el-button>
      </div>
    </section>

    <el-empty v-if="!loading && !organizations.length" description="还没有组织，先创建一个团队空间" />

    <section v-else v-loading="loading" class="organization-grid">
      <el-card v-for="organization in organizations" :key="organization.id" shadow="never" class="organization-card">
        <div class="organization-card__header">
          <div>
            <h3>{{ organization.name }}</h3>
            <p>{{ organization.description || '暂无描述' }}</p>
          </div>
          <el-tag :type="organization.currentUserRole === 'OWNER' ? 'success' : 'info'">
            {{ organization.currentUserRole }}
          </el-tag>
        </div>

        <div class="organization-stats">
          <span>
            <strong>{{ organization.memberCount }}</strong>
            成员
          </span>
          <span>
            <strong>{{ organization.projectCount }}</strong>
            项目
          </span>
          <span>
            <strong>{{ organization.ownerName }}</strong>
            负责人
          </span>
        </div>

        <div class="card-actions">
          <el-button :icon="UserFilled" @click="openMembers(organization)">成员</el-button>
        </div>
      </el-card>
    </section>

    <el-dialog v-model="createVisible" title="新建组织" width="480px">
      <el-form label-position="top">
        <el-form-item label="组织名称">
          <el-input v-model="createForm.name" maxlength="128" placeholder="CodeCollab 产品组" />
        </el-form-item>
        <el-form-item label="组织描述">
          <el-input v-model="createForm.description" type="textarea" :rows="3" maxlength="512" placeholder="这个组织负责什么" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrganization">创建</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="membersVisible" :title="selectedOrganization?.name || '组织成员'" size="520px">
      <div class="drawer-stack">
        <el-form v-if="canManageSelectedOrganization" inline>
          <el-form-item>
            <el-input v-model="memberForm.username" placeholder="用户名，如 pm" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="memberForm.memberRole" class="role-select">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="成员" value="MEMBER" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitMember">保存成员</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="memberLoading" :data="members">
          <el-table-column prop="nickname" label="昵称" min-width="120" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="role" label="角色" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
        </el-table>
      </div>
    </el-drawer>
  </div>
</template>
