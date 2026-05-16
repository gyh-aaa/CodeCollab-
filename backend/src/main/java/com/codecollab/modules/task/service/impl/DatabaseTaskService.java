package com.codecollab.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codecollab.common.BusinessException;
import com.codecollab.common.ErrorCode;
import com.codecollab.modules.project.dto.BoardColumnResponse;
import com.codecollab.modules.project.dto.TaskCardResponse;
import com.codecollab.modules.project.entity.Project;
import com.codecollab.modules.project.entity.ProjectMember;
import com.codecollab.modules.project.mapper.ProjectMapper;
import com.codecollab.modules.project.mapper.ProjectMemberMapper;
import com.codecollab.modules.system.entity.SysUser;
import com.codecollab.modules.system.mapper.SysUserMapper;
import com.codecollab.modules.task.dto.ChangeTaskStatusRequest;
import com.codecollab.modules.task.dto.CreateTaskLabelRequest;
import com.codecollab.modules.task.dto.CreateTaskRequest;
import com.codecollab.modules.task.dto.TaskLabelResponse;
import com.codecollab.modules.task.dto.TaskLogResponse;
import com.codecollab.modules.task.dto.TaskResponse;
import com.codecollab.modules.task.dto.UpdateTaskRequest;
import com.codecollab.modules.task.entity.OperationLog;
import com.codecollab.modules.task.entity.Task;
import com.codecollab.modules.task.entity.TaskLabel;
import com.codecollab.modules.task.entity.TaskLabelRel;
import com.codecollab.modules.task.mapper.OperationLogMapper;
import com.codecollab.modules.task.mapper.TaskLabelMapper;
import com.codecollab.modules.task.mapper.TaskLabelRelMapper;
import com.codecollab.modules.task.mapper.TaskMapper;
import com.codecollab.modules.task.service.TaskService;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Profile("mysql")
public class DatabaseTaskService implements TaskService {

    private static final Set<String> TASK_STATUSES = Set.of("TODO", "IN_PROGRESS", "DONE");
    private static final Set<String> TASK_PRIORITIES = Set.of("HIGH", "MEDIUM", "LOW");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Map<String, String> BOARD_TITLES = Map.of(
        "TODO", "待处理",
        "IN_PROGRESS", "进行中",
        "DONE", "已完成"
    );

    private final TaskMapper taskMapper;
    private final TaskLabelMapper taskLabelMapper;
    private final TaskLabelRelMapper taskLabelRelMapper;
    private final OperationLogMapper operationLogMapper;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysUserMapper sysUserMapper;

    public DatabaseTaskService(
        TaskMapper taskMapper,
        TaskLabelMapper taskLabelMapper,
        TaskLabelRelMapper taskLabelRelMapper,
        OperationLogMapper operationLogMapper,
        ProjectMapper projectMapper,
        ProjectMemberMapper projectMemberMapper,
        SysUserMapper sysUserMapper
    ) {
        this.taskMapper = taskMapper;
        this.taskLabelMapper = taskLabelMapper;
        this.taskLabelRelMapper = taskLabelRelMapper;
        this.operationLogMapper = operationLogMapper;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public List<TaskResponse> listTasks(String username, Long projectId, String status, Long assigneeId, String keyword) {
        SysUser currentUser = requireUser(username);
        requireProjectMember(projectId, currentUser.getId());
        LambdaQueryWrapper<Task> query = new LambdaQueryWrapper<Task>()
            .eq(Task::getProjectId, projectId)
            .eq(Task::getDeleted, 0)
            .orderByAsc(Task::getStatus)
            .orderByAsc(Task::getSortOrder)
            .orderByDesc(Task::getUpdatedAt);
        if (StringUtils.hasText(status)) {
            query.eq(Task::getStatus, normalizeStatus(status));
        }
        if (assigneeId != null) {
            query.eq(Task::getAssigneeId, assigneeId);
        }
        if (StringUtils.hasText(keyword)) {
            query.like(Task::getTitle, keyword.trim());
        }
        return taskMapper.selectList(query).stream()
            .map(this::toTaskResponse)
            .toList();
    }

    @Override
    @Transactional
    public TaskResponse createTask(String username, Long projectId, CreateTaskRequest request) {
        SysUser currentUser = requireUser(username);
        requireProjectManager(projectId, currentUser.getId());
        validateAssignee(projectId, request.assigneeId());
        validateParent(projectId, request.parentId());

        Task task = new Task();
        task.setProjectId(projectId);
        task.setParentId(request.parentId());
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setStatus(normalizeStatus(defaultString(request.status(), "TODO")));
        task.setPriority(normalizePriority(defaultString(request.priority(), "MEDIUM")));
        task.setAssigneeId(request.assigneeId());
        task.setReporterId(currentUser.getId());
        task.setDueDate(request.dueDate());
        task.setSortOrder(nextSortOrder(projectId, task.getStatus()));
        task.setDeleted(0);
        taskMapper.insert(task);
        replaceLabels(projectId, task.getId(), request.labelIds());
        log(currentUser.getId(), task.getId(), "CREATE_TASK", "创建任务：" + task.getTitle());
        return toTaskResponse(taskMapper.selectById(task.getId()));
    }

    @Override
    public TaskResponse getTask(String username, Long taskId) {
        SysUser currentUser = requireUser(username);
        Task task = requireTask(taskId);
        requireProjectMember(task.getProjectId(), currentUser.getId());
        return toTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String username, Long taskId, UpdateTaskRequest request) {
        SysUser currentUser = requireUser(username);
        Task task = requireTask(taskId);
        requireProjectManager(task.getProjectId(), currentUser.getId());
        validateAssignee(task.getProjectId(), request.assigneeId());

        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setPriority(normalizePriority(defaultString(request.priority(), task.getPriority())));
        task.setAssigneeId(request.assigneeId());
        task.setDueDate(request.dueDate());
        taskMapper.updateById(task);
        replaceLabels(task.getProjectId(), task.getId(), request.labelIds());
        log(currentUser.getId(), task.getId(), "UPDATE_TASK", "更新任务：" + task.getTitle());
        return toTaskResponse(taskMapper.selectById(task.getId()));
    }

    @Override
    @Transactional
    public TaskResponse changeStatus(String username, Long taskId, ChangeTaskStatusRequest request) {
        SysUser currentUser = requireUser(username);
        Task task = requireTask(taskId);
        requireProjectMember(task.getProjectId(), currentUser.getId());
        String nextStatus = normalizeStatus(request.status());
        String previousStatus = task.getStatus();
        task.setStatus(nextStatus);
        task.setSortOrder(nextSortOrder(task.getProjectId(), nextStatus));
        taskMapper.updateById(task);
        log(currentUser.getId(), task.getId(), "CHANGE_STATUS", previousStatus + " -> " + nextStatus);
        return toTaskResponse(taskMapper.selectById(task.getId()));
    }

    @Override
    @Transactional
    public void deleteTask(String username, Long taskId) {
        SysUser currentUser = requireUser(username);
        Task task = requireTask(taskId);
        requireProjectManager(task.getProjectId(), currentUser.getId());
        task.setDeleted(1);
        taskMapper.updateById(task);
        log(currentUser.getId(), task.getId(), "DELETE_TASK", "删除任务：" + task.getTitle());
    }

    @Override
    public List<TaskLabelResponse> listLabels(String username, Long projectId) {
        SysUser currentUser = requireUser(username);
        requireProjectMember(projectId, currentUser.getId());
        return taskLabelMapper.selectList(new LambdaQueryWrapper<TaskLabel>()
                .eq(TaskLabel::getProjectId, projectId)
                .orderByAsc(TaskLabel::getCreatedAt))
            .stream()
            .map(this::toLabelResponse)
            .toList();
    }

    @Override
    @Transactional
    public TaskLabelResponse createLabel(String username, Long projectId, CreateTaskLabelRequest request) {
        SysUser currentUser = requireUser(username);
        requireProjectManager(projectId, currentUser.getId());
        TaskLabel label = new TaskLabel();
        label.setProjectId(projectId);
        label.setName(request.name().trim());
        label.setColor(defaultString(request.color(), "#409EFF"));
        taskLabelMapper.insert(label);
        return toLabelResponse(label);
    }

    @Override
    public List<TaskLogResponse> listLogs(String username, Long taskId) {
        SysUser currentUser = requireUser(username);
        Task task = requireTask(taskId);
        requireProjectMember(task.getProjectId(), currentUser.getId());
        return operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getBizType, "TASK")
                .eq(OperationLog::getBizId, taskId)
                .orderByDesc(OperationLog::getCreatedAt))
            .stream()
            .map(this::toLogResponse)
            .toList();
    }

    @Override
    public List<BoardColumnResponse> board(String username, Long projectId) {
        SysUser currentUser = requireUser(username);
        requireProjectMember(projectId, currentUser.getId());
        List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
            .eq(Task::getProjectId, projectId)
            .eq(Task::getDeleted, 0)
            .orderByAsc(Task::getSortOrder)
            .orderByDesc(Task::getUpdatedAt));
        return TASK_STATUSES.stream()
            .sorted(Comparator.comparingInt(this::statusOrder))
            .map(status -> new BoardColumnResponse(
                status,
                BOARD_TITLES.get(status),
                tasks.stream()
                    .filter(task -> status.equals(task.getStatus()))
                    .map(this::toTaskCard)
                    .toList()
            ))
            .toList();
    }

    private TaskCardResponse toTaskCard(Task task) {
        return new TaskCardResponse(
            task.getId(),
            task.getTitle(),
            task.getPriority(),
            task.getAssigneeId() == null ? "未指派" : requireUser(task.getAssigneeId()).getNickname(),
            task.getDueDate() == null ? "-" : task.getDueDate().toLocalDate().format(DATE_FORMATTER),
            taskLabels(task.getId()).stream().map(TaskLabelResponse::name).toList()
        );
    }

    private TaskResponse toTaskResponse(Task task) {
        SysUser reporter = requireUser(task.getReporterId());
        SysUser assignee = task.getAssigneeId() == null ? null : requireUser(task.getAssigneeId());
        return new TaskResponse(
            task.getId(),
            task.getProjectId(),
            task.getParentId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getAssigneeId(),
            assignee == null ? null : assignee.getNickname(),
            task.getReporterId(),
            reporter.getNickname(),
            task.getDueDate(),
            task.getSortOrder(),
            taskLabels(task.getId()),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }

    private TaskLabelResponse toLabelResponse(TaskLabel label) {
        return new TaskLabelResponse(label.getId(), label.getProjectId(), label.getName(), label.getColor());
    }

    private TaskLogResponse toLogResponse(OperationLog log) {
        SysUser operator = requireUser(log.getOperatorId());
        return new TaskLogResponse(log.getId(), log.getOperatorId(), operator.getNickname(), log.getAction(), log.getDetail(), log.getCreatedAt());
    }

    private List<TaskLabelResponse> taskLabels(Long taskId) {
        List<TaskLabelRel> relations = taskLabelRelMapper.selectList(new LambdaQueryWrapper<TaskLabelRel>()
            .eq(TaskLabelRel::getTaskId, taskId));
        if (relations.isEmpty()) {
            return List.of();
        }
        return relations.stream()
            .map(relation -> taskLabelMapper.selectById(relation.getLabelId()))
            .filter(label -> label != null)
            .map(this::toLabelResponse)
            .toList();
    }

    private void replaceLabels(Long projectId, Long taskId, List<Long> labelIds) {
        taskLabelRelMapper.delete(new LambdaQueryWrapper<TaskLabelRel>().eq(TaskLabelRel::getTaskId, taskId));
        if (labelIds == null || labelIds.isEmpty()) {
            return;
        }
        for (Long labelId : labelIds) {
            TaskLabel label = taskLabelMapper.selectById(labelId);
            if (label == null || !projectId.equals(label.getProjectId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "任务标签不属于当前项目");
            }
            TaskLabelRel relation = new TaskLabelRel();
            relation.setTaskId(taskId);
            relation.setLabelId(labelId);
            taskLabelRelMapper.insert(relation);
        }
    }

    private int nextSortOrder(Long projectId, String status) {
        Task latest = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
            .eq(Task::getProjectId, projectId)
            .eq(Task::getStatus, status)
            .eq(Task::getDeleted, 0)
            .orderByDesc(Task::getSortOrder)
            .last("LIMIT 1"));
        return latest == null || latest.getSortOrder() == null ? 10 : latest.getSortOrder() + 10;
    }

    private void log(Long operatorId, Long taskId, String action, String detail) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setBizType("TASK");
        log.setBizId(taskId);
        log.setAction(action);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }

    private void validateParent(Long projectId, Long parentId) {
        if (parentId == null) {
            return;
        }
        Task parent = requireTask(parentId);
        if (!projectId.equals(parent.getProjectId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "父任务不属于当前项目");
        }
    }

    private void validateAssignee(Long projectId, Long assigneeId) {
        if (assigneeId == null) {
            return;
        }
        requireUser(assigneeId);
        requireProjectMember(projectId, assigneeId);
    }

    private SysUser requireUser(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, username)
            .eq(SysUser::getDeleted, 0)
            .last("LIMIT 1"));
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private SysUser requireUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private Project requireProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null || Integer.valueOf(1).equals(project.getDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "项目不存在");
        }
        return project;
    }

    private Task requireTask(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null || Integer.valueOf(1).equals(task.getDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "任务不存在");
        }
        return task;
    }

    private void requireProjectMember(Long projectId, Long userId) {
        requireProject(projectId);
        ProjectMember member = projectMember(projectId, userId);
        if (member == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "你不是该项目成员");
        }
    }

    private void requireProjectManager(Long projectId, Long userId) {
        requireProject(projectId);
        ProjectMember member = projectMember(projectId, userId);
        if (member == null || (!"OWNER".equals(member.getProjectRole()) && !"MANAGER".equals(member.getProjectRole()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有项目任务管理权限");
        }
    }

    private ProjectMember projectMember(Long projectId, Long userId) {
        return projectMemberMapper.selectOne(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, projectId)
            .eq(ProjectMember::getUserId, userId)
            .last("LIMIT 1"));
    }

    private String normalizeStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!TASK_STATUSES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的任务状态");
        }
        return normalized;
    }

    private String normalizePriority(String priority) {
        String normalized = priority.trim().toUpperCase(Locale.ROOT);
        if (!TASK_PRIORITIES.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的任务优先级");
        }
        return normalized;
    }

    private String defaultString(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private int statusOrder(String status) {
        return switch (status) {
            case "TODO" -> 1;
            case "IN_PROGRESS" -> 2;
            case "DONE" -> 3;
            default -> 99;
        };
    }
}
