import http from './http'

import type { BoardColumn, ProjectSummary } from '@/types/project'

export function listProjectsApi() {
  return http.get<ProjectSummary[]>('/projects')
}

export function getProjectBoardApi(projectId: number) {
  return http.get<BoardColumn[]>(`/projects/${projectId}/board`)
}
