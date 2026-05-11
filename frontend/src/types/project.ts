export interface ProjectSummary {
  id: number
  name: string
  code: string
  status: 'ACTIVE' | 'PLANNING' | 'ARCHIVED'
  totalTasks: number
  completedTasks: number
  ownerName: string
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
