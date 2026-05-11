<script setup lang="ts">
const notifications = [
  { id: 1, title: '任务指派', content: '你被指派到「搭建项目基础工程」。', time: '今天 09:30' },
  { id: 2, title: '评论提醒', content: '项目负责人评论了「梳理任务详情交互」。', time: '昨天 18:10' },
  { id: 3, title: '截止提醒', content: '「统一接口响应格式」将在 2 天后到期。', time: '昨天 10:20' },
]
</script>

<template>
  <div class="page-stack">
    <section class="toolbar-row">
      <div>
        <h2>通知中心</h2>
        <p>WebSocket 实时通知会在第 6 阶段接入。</p>
      </div>
      <el-button type="primary">全部已读</el-button>
    </section>

    <el-card shadow="never">
      <div class="notification-list">
        <article v-for="item in notifications" :key="item.id" class="notification-item">
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ item.content }}</p>
          </div>
          <span>{{ item.time }}</span>
        </article>
      </div>
    </el-card>
  </div>
</template>
