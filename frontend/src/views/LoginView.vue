<script setup lang="ts">
import { Lock, User } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'Admin@123456',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submit() {
  const valid = await formRef.value?.validate()
  if (!valid) {
    return
  }

  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.replace(String(route.query.redirect || '/dashboard'))
  } catch {
    // 请求层已经弹出错误提示，这里只负责结束登录流程。
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="login-panel">
      <div class="login-copy">
        <div class="brand large">
          <div class="brand-mark">C</div>
          <div>
            <strong>CodeCollab</strong>
            <span>智能项目协作系统</span>
          </div>
        </div>
        <h1>让项目、任务和团队状态保持清晰</h1>
        <p>面向真实企业后台场景的 Vue 3 + Spring Boot 全栈练习项目。</p>
      </div>

      <el-card class="login-card" shadow="never">
        <template #header>
          <div>
            <h2>登录</h2>
            <p>使用开发账号进入工作台</p>
          </div>
        </template>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" :prefix-icon="User" placeholder="admin" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              :prefix-icon="Lock"
              placeholder="Admin@123456"
              show-password
              type="password"
            />
          </el-form-item>

          <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">
            进入系统
          </el-button>
        </el-form>
      </el-card>
    </section>
  </main>
</template>
