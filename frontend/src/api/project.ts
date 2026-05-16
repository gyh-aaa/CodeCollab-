import http from './http'

import type {
  AddMemberPayload,
  BoardColumn,
  CreateOrganizationPayload,
  CreateProjectPayload,
  CreateTaskLabelPayload,
  CreateTaskPayload,
  Member,
  Organization,
  ProjectDetail,
  ProjectSummary,
  Task,
  TaskLabel,
  TaskLog,
  UpdateTaskPayload,
  UpdateProjectPayload,
} from '@/types/project'

export function listOrganizationsApi() {
  return http.get<Organization[]>('/organizations')
}

export function createOrganizationApi(payload: CreateOrganizationPayload) {
  return http.post<Organization>('/organizations', payload)
}

export function listOrganizationMembersApi(organizationId: number) {
  return http.get<Member[]>(`/organizations/${organizationId}/members`)
}

export function addOrganizationMemberApi(organizationId: number, payload: AddMemberPayload) {
  return http.post<Member>(`/organizations/${organizationId}/members`, {
    username: payload.username,
    memberRole: payload.memberRole,
  })
}

export function listProjectsApi(organizationId?: number) {
  const config = organizationId ? { params: { organizationId } } : {}
  return http.get<ProjectSummary[]>('/projects', config)
}

export function createProjectApi(payload: CreateProjectPayload) {
  return http.post<ProjectDetail>('/projects', payload)
}

export function getProjectApi(projectId: number) {
  return http.get<ProjectDetail>(`/projects/${projectId}`)
}

export function updateProjectApi(projectId: number, payload: UpdateProjectPayload) {
  return http.put<ProjectDetail>(`/projects/${projectId}`, payload)
}

export function archiveProjectApi(projectId: number) {
  return http.post<ProjectDetail>(`/projects/${projectId}/archive`)
}

export function listProjectMembersApi(projectId: number) {
  return http.get<Member[]>(`/projects/${projectId}/members`)
}

export function addProjectMemberApi(projectId: number, payload: AddMemberPayload) {
  return http.post<Member>(`/projects/${projectId}/members`, {
    username: payload.username,
    projectRole: payload.projectRole,
  })
}

export function getProjectBoardApi(projectId: number) {
  return http.get<BoardColumn[]>(`/projects/${projectId}/board`)
}

export function listTasksApi(projectId: number, params: { status?: string; assigneeId?: number; keyword?: string } = {}) {
  return http.get<Task[]>(`/projects/${projectId}/tasks`, { params })
}

export function createTaskApi(projectId: number, payload: CreateTaskPayload) {
  return http.post<Task>(`/projects/${projectId}/tasks`, payload)
}

export function getTaskApi(taskId: number) {
  return http.get<Task>(`/tasks/${taskId}`)
}

export function updateTaskApi(taskId: number, payload: UpdateTaskPayload) {
  return http.put<Task>(`/tasks/${taskId}`, payload)
}

export function changeTaskStatusApi(taskId: number, status: Task['status']) {
  return http.post<Task>(`/tasks/${taskId}/status`, { status })
}

export function deleteTaskApi(taskId: number) {
  return http.delete<void>(`/tasks/${taskId}`)
}

export function listTaskLabelsApi(projectId: number) {
  return http.get<TaskLabel[]>(`/projects/${projectId}/task-labels`)
}

export function createTaskLabelApi(projectId: number, payload: CreateTaskLabelPayload) {
  return http.post<TaskLabel>(`/projects/${projectId}/task-labels`, payload)
}

export function listTaskLogsApi(taskId: number) {
  return http.get<TaskLog[]>(`/tasks/${taskId}/logs`)
}
