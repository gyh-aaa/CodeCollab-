package com.codecollab.modules.task.service.impl;

import com.codecollab.common.BusinessException;
import com.codecollab.common.ErrorCode;
import com.codecollab.modules.project.dto.BoardColumnResponse;
import com.codecollab.modules.project.dto.TaskCardResponse;
import com.codecollab.modules.task.dto.ChangeTaskStatusRequest;
import com.codecollab.modules.task.dto.CreateTaskLabelRequest;
import com.codecollab.modules.task.dto.CreateTaskRequest;
import com.codecollab.modules.task.dto.TaskLabelResponse;
import com.codecollab.modules.task.dto.TaskLogResponse;
import com.codecollab.modules.task.dto.TaskResponse;
import com.codecollab.modules.task.dto.UpdateTaskRequest;
import com.codecollab.modules.task.service.TaskService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Profile("demo")
public class DemoTaskService implements TaskService {

    private static final Set<String> STATUSES = Set.of("TODO", "IN_PROGRESS", "DONE");
    private static final Set<String> PRIORITIES = Set.of("HIGH", "MEDIUM", "LOW");
    private static final Map<String, String> COLUMN_TITLES = Map.of(
        "TODO", "待处理",
        "IN_PROGRESS", "进行中",
        "DONE", "已完成"
    );
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AtomicLong taskId = new AtomicLong(10);
    private final AtomicLong labelId = new AtomicLong(6);
    private final AtomicLong logId = new AtomicLong(20);
    private final List<DemoTask> tasks = new ArrayList<>();
    private final List<TaskLabelResponse> labels = new ArrayList<>();
    private final List<DemoTaskLog> logs = new ArrayList<>();

    public DemoTaskService() {
        labels.add(new TaskLabelResponse(1L, 1L, "权限", "#7c3aed"));
        labels.add(new TaskLabelResponse(2L, 1L, "后端", "#2563eb"));
        labels.add(new TaskLabelResponse(3L, 1L, "前端", "#16a34a"));
        labels.add(new TaskLabelResponse(4L, 1L, "工程化", "#f59e0b"));
        labels.add(new TaskLabelResponse(5L, 2L, "移动端", "#0f766e"));
        tasks.add(new DemoTask(1L, 1L, null, "设计 RBAC 数据模型", "整理用户、角色、菜单和权限的关系", "TODO", "HIGH", 1L, "系统管理员", 1L, "系统管理员", LocalDateTime.now().plusDays(2), 10, List.of(1L, 2L)));
        tasks.add(new DemoTask(2L, 1L, null, "梳理任务详情交互", "明确任务抽屉字段与操作入口", "TODO", "MEDIUM", 2L, "项目负责人", 1L, "系统管理员", LocalDateTime.now().plusDays(4), 20, List.of(3L)));
        tasks.add(new DemoTask(3L, 1L, null, "搭建项目基础工程", "初始化前后端工程结构", "DONE", "HIGH", 3L, "研发成员", 1L, "系统管理员", LocalDateTime.now().minusDays(1), 10, List.of(4L)));
    }

    @Override
    public List<TaskResponse> listTasks(String username, Long projectId, String status, Long assigneeId, String keyword) {
        return tasks.stream()
            .filter(task -> task.projectId().equals(projectId))
            .filter(task -> !task.deleted)
            .filter(task -> !StringUtils.hasText(status) || normalizeStatus(status).equals(task.status))
            .filter(task -> assigneeId == null || assigneeId.equals(task.assigneeId()))
            .filter(task -> !StringUtils.hasText(keyword) || task.title().contains(keyword.trim()))
            .sorted(Comparator.comparing(DemoTask::status).thenComparing(DemoTask::sortOrder))
            .map(this::toResponse)
            .toList();
    }

    @Override
    public TaskResponse createTask(String username, Long projectId, CreateTaskRequest request) {
        DemoTask task = new DemoTask(
            taskId.getAndIncrement(),
            projectId,
            request.parentId(),
            request.title().trim(),
            request.description(),
            normalizeStatus(StringUtils.hasText(request.status()) ? request.status() : "TODO"),
            normalizePriority(StringUtils.hasText(request.priority()) ? request.priority() : "MEDIUM"),
            request.assigneeId(),
            nameOf(request.assigneeId()),
            1L,
            "系统管理员",
            request.dueDate(),
            nextSortOrder(projectId, StringUtils.hasText(request.status()) ? request.status() : "TODO"),
            request.labelIds() == null ? List.of() : request.labelIds()
        );
        tasks.add(task);
        addLog(task.id(), "CREATE_TASK", "创建任务：" + task.title());
        return toResponse(task);
    }

    @Override
    public TaskResponse getTask(String username, Long taskId) {
        return toResponse(task(taskId));
    }

    @Override
    public TaskResponse updateTask(String username, Long taskId, UpdateTaskRequest request) {
        DemoTask task = task(taskId);
        task.title = request.title().trim();
        task.description = request.description();
        task.priority = normalizePriority(StringUtils.hasText(request.priority()) ? request.priority() : task.priority());
        task.assigneeId = request.assigneeId();
        task.assigneeName = nameOf(request.assigneeId());
        task.dueDate = request.dueDate();
        task.labelIds = request.labelIds() == null ? List.of() : request.labelIds();
        task.updatedAt = LocalDateTime.now();
        addLog(task.id(), "UPDATE_TASK", "更新任务：" + task.title());
        return toResponse(task);
    }

    @Override
    public TaskResponse changeStatus(String username, Long taskId, ChangeTaskStatusRequest request) {
        DemoTask task = task(taskId);
        String previous = task.status;
        task.status = normalizeStatus(request.status());
        task.sortOrder = nextSortOrder(task.projectId(), task.status());
        task.updatedAt = LocalDateTime.now();
        addLog(task.id(), "CHANGE_STATUS", previous + " -> " + task.status());
        return toResponse(task);
    }

    @Override
    public void deleteTask(String username, Long taskId) {
        DemoTask task = task(taskId);
        task.deleted = true;
        task.updatedAt = LocalDateTime.now();
        addLog(task.id(), "DELETE_TASK", "删除任务：" + task.title());
    }

    @Override
    public List<TaskLabelResponse> listLabels(String username, Long projectId) {
        return labels.stream()
            .filter(label -> label.projectId().equals(projectId))
            .toList();
    }

    @Override
    public TaskLabelResponse createLabel(String username, Long projectId, CreateTaskLabelRequest request) {
        TaskLabelResponse label = new TaskLabelResponse(labelId.getAndIncrement(), projectId, request.name().trim(), StringUtils.hasText(request.color()) ? request.color() : "#409EFF");
        labels.add(label);
        return label;
    }

    @Override
    public List<TaskLogResponse> listLogs(String username, Long taskId) {
        task(taskId);
        return logs.stream()
            .filter(log -> log.taskId().equals(taskId))
            .sorted(Comparator.comparing(DemoTaskLog::createdAt).reversed())
            .map(log -> new TaskLogResponse(log.id(), 1L, "系统管理员", log.action(), log.detail(), log.createdAt()))
            .toList();
    }

    @Override
    public List<BoardColumnResponse> board(String username, Long projectId) {
        return STATUSES.stream()
            .sorted(Comparator.comparingInt(this::statusOrder))
            .map(status -> new BoardColumnResponse(
                status,
                COLUMN_TITLES.get(status),
                tasks.stream()
                    .filter(task -> task.projectId().equals(projectId))
                    .filter(task -> !task.deleted)
                    .filter(task -> status.equals(task.status()))
                    .sorted(Comparator.comparing(DemoTask::sortOrder))
                    .map(this::toCard)
                    .toList()
            ))
            .toList();
    }

    private TaskResponse toResponse(DemoTask task) {
        return new TaskResponse(
            task.id(),
            task.projectId(),
            task.parentId(),
            task.title(),
            task.description(),
            task.status(),
            task.priority(),
            task.assigneeId(),
            task.assigneeName(),
            task.reporterId(),
            task.reporterName(),
            task.dueDate(),
            task.sortOrder(),
            taskLabels(task),
            task.createdAt(),
            task.updatedAt()
        );
    }

    private TaskCardResponse toCard(DemoTask task) {
        return new TaskCardResponse(
            task.id(),
            task.title(),
            task.priority(),
            task.assigneeName() == null ? "未指派" : task.assigneeName(),
            task.dueDate() == null ? "-" : task.dueDate().toLocalDate().format(DATE_FORMATTER),
            taskLabels(task).stream().map(TaskLabelResponse::name).toList()
        );
    }

    private List<TaskLabelResponse> taskLabels(DemoTask task) {
        return labels.stream()
            .filter(label -> task.labelIds().contains(label.id()))
            .toList();
    }

    private DemoTask task(Long id) {
        return tasks.stream()
            .filter(task -> task.id().equals(id))
            .filter(task -> !task.deleted)
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "任务不存在"));
    }

    private void addLog(Long taskId, String action, String detail) {
        logs.add(new DemoTaskLog(logId.getAndIncrement(), taskId, action, detail, LocalDateTime.now()));
    }

    private int nextSortOrder(Long projectId, String status) {
        return tasks.stream()
            .filter(task -> task.projectId().equals(projectId))
            .filter(task -> normalizeStatus(status).equals(task.status()))
            .map(DemoTask::sortOrder)
            .max(Integer::compareTo)
            .orElse(0) + 10;
    }

    private String normalizeStatus(String status) {
        String normalized = status.trim().toUpperCase();
        if (!STATUSES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的任务状态");
        }
        return normalized;
    }

    private String normalizePriority(String priority) {
        String normalized = priority.trim().toUpperCase();
        if (!PRIORITIES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的任务优先级");
        }
        return normalized;
    }

    private String nameOf(Long userId) {
        if (userId == null) {
            return null;
        }
        if (userId == 2L) {
            return "项目负责人";
        }
        if (userId == 3L) {
            return "研发成员";
        }
        return "系统管理员";
    }

    private int statusOrder(String status) {
        return switch (status) {
            case "TODO" -> 1;
            case "IN_PROGRESS" -> 2;
            case "DONE" -> 3;
            default -> 99;
        };
    }

    private static class DemoTask {
        private final Long id;
        private final Long projectId;
        private final Long parentId;
        private final Long reporterId;
        private final String reporterName;
        private final LocalDateTime createdAt;
        private String title;
        private String description;
        private String status;
        private String priority;
        private Long assigneeId;
        private String assigneeName;
        private LocalDateTime dueDate;
        private Integer sortOrder;
        private List<Long> labelIds;
        private LocalDateTime updatedAt;
        private boolean deleted;

        private DemoTask(Long id, Long projectId, Long parentId, String title, String description, String status, String priority, Long assigneeId, String assigneeName, Long reporterId, String reporterName, LocalDateTime dueDate, Integer sortOrder, List<Long> labelIds) {
            this.id = id;
            this.projectId = projectId;
            this.parentId = parentId;
            this.title = title;
            this.description = description;
            this.status = status;
            this.priority = priority;
            this.assigneeId = assigneeId;
            this.assigneeName = assigneeName;
            this.reporterId = reporterId;
            this.reporterName = reporterName;
            this.dueDate = dueDate;
            this.sortOrder = sortOrder;
            this.labelIds = labelIds;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = this.createdAt;
        }

        private Long id() { return id; }
        private Long projectId() { return projectId; }
        private Long parentId() { return parentId; }
        private String title() { return title; }
        private String description() { return description; }
        private String status() { return status; }
        private String priority() { return priority; }
        private Long assigneeId() { return assigneeId; }
        private String assigneeName() { return assigneeName; }
        private Long reporterId() { return reporterId; }
        private String reporterName() { return reporterName; }
        private LocalDateTime dueDate() { return dueDate; }
        private Integer sortOrder() { return sortOrder; }
        private List<Long> labelIds() { return labelIds; }
        private LocalDateTime createdAt() { return createdAt; }
        private LocalDateTime updatedAt() { return updatedAt; }
    }

    private record DemoTaskLog(Long id, Long taskId, String action, String detail, LocalDateTime createdAt) {
    }
}
