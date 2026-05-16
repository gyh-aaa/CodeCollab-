export type ProjectStatus = 'ACTIVE' | 'PLANNING' | 'ARCHIVED'
export type OrganizationRole = 'OWNER' | 'ADMIN' | 'MEMBER'
export type ProjectRole = 'OWNER' | 'MANAGER' | 'DEVELOPER' | 'VIEWER'

export interface Organization {
  id: number
  name: string
  description?: string
  ownerId: number
  ownerName: string
  status: 'ACTIVE' | 'ARCHIVED'
  currentUserRole: OrganizationRole
  memberCount: number
  projectCount: number
  createdAt: string
}

export interface Member {
  id: number
  userId: number
  username: string
  nickname: string
  email?: string
  role: string
  joinedAt: string
}

export interface ProjectSummary {
  id: number
  organizationId: number
  organizationName: string
  name: string
  code: string
  description?: string
  status: ProjectStatus
  totalTasks: number
  completedTasks: number
  ownerName: string
  currentUserRole: ProjectRole
}

export interface ProjectDetail extends ProjectSummary {
  ownerId: number
  startDate?: string
  endDate?: string
  memberCount: number
}

export interface CreateOrganizationPayload {
  name: string
  description?: string
}

export interface CreateProjectPayload {
  organizationId: number
  name: string
  code: string
  description?: string
  startDate?: string
  endDate?: string
}

export interface UpdateProjectPayload {
  name: string
  description?: string
  startDate?: string
  endDate?: string
}

export interface AddMemberPayload {
  username: string
  memberRole?: OrganizationRole
  projectRole?: ProjectRole
}

export interface TaskCard {
  id: number
  title: string
  priority: 'HIGH' | 'MEDIUM' | 'LOW'
  assigneeName: string
  dueDate: string
  labels: string[]
}

export interface BoardColumn {
  key: string
  title: string
  tasks: TaskCard[]
}

export interface TaskLabel {
  id: number
  projectId: number
  name: string
  color: string
}

export interface Task {
  id: number
  projectId: number
  parentId?: number
  title: string
  description?: string
  status: 'TODO' | 'IN_PROGRESS' | 'DONE'
  priority: 'HIGH' | 'MEDIUM' | 'LOW'
  assigneeId?: number
  assigneeName?: string
  reporterId: number
  reporterName: string
  dueDate?: string
  sortOrder: number
  labels: TaskLabel[]
  createdAt: string
  updatedAt: string
}

export interface TaskLog {
  id: number
  operatorId: number
  operatorName: string
  action: string
  detail: string
  createdAt: string
}

export interface CreateTaskPayload {
  parentId?: number
  title: string
  description?: string
  status?: Task['status']
  priority?: Task['priority']
  assigneeId?: number
  dueDate?: string
  labelIds?: number[]
}

export interface UpdateTaskPayload {
  title: string
  description?: string
  priority?: Task['priority']
  assigneeId?: number
  dueDate?: string
  labelIds?: number[]
}

export interface CreateTaskLabelPayload {
  name: string
  color?: string
}
