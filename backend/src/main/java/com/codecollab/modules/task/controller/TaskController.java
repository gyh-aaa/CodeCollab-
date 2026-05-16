package com.codecollab.modules.task.controller;

import com.codecollab.common.ApiResponse;
import com.codecollab.modules.task.dto.ChangeTaskStatusRequest;
import com.codecollab.modules.task.dto.CreateTaskLabelRequest;
import com.codecollab.modules.task.dto.CreateTaskRequest;
import com.codecollab.modules.task.dto.TaskLabelResponse;
import com.codecollab.modules.task.dto.TaskLogResponse;
import com.codecollab.modules.task.dto.TaskResponse;
import com.codecollab.modules.task.dto.UpdateTaskRequest;
import com.codecollab.modules.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "任务管理", description = "任务 CRUD、状态流转、标签和操作日志接口")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    @Operation(summary = "查询项目任务", description = "按状态、负责人和关键字查询项目任务。")
    public ApiResponse<List<TaskResponse>> listTasks(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Long assigneeId,
        @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(taskService.listTasks(authentication.getName(), projectId, status, assigneeId, keyword));
    }

    @PostMapping("/api/projects/{projectId}/tasks")
    @Operation(summary = "创建任务", description = "项目负责人或管理员可创建任务。")
    public ApiResponse<TaskResponse> createTask(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId,
        @Valid @RequestBody CreateTaskRequest request
    ) {
        return ApiResponse.ok(taskService.createTask(authentication.getName(), projectId, request));
    }

    @GetMapping("/api/tasks/{taskId}")
    @Operation(summary = "查询任务详情", description = "返回任务字段、负责人、标签和时间信息。")
    public ApiResponse<TaskResponse> getTask(
        Authentication authentication,
        @Parameter(description = "任务 ID", example = "1", required = true)
        @PathVariable Long taskId
    ) {
        return ApiResponse.ok(taskService.getTask(authentication.getName(), taskId));
    }

    @PutMapping("/api/tasks/{taskId}")
    @Operation(summary = "更新任务", description = "项目负责人或管理员可更新任务。")
    public ApiResponse<TaskResponse> updateTask(
        Authentication authentication,
        @Parameter(description = "任务 ID", example = "1", required = true)
        @PathVariable Long taskId,
        @Valid @RequestBody UpdateTaskRequest request
    ) {
        return ApiResponse.ok(taskService.updateTask(authentication.getName(), taskId, request));
    }

    @PostMapping("/api/tasks/{taskId}/status")
    @Operation(summary = "任务状态流转", description = "项目成员可把任务流转到 TODO、IN_PROGRESS 或 DONE。")
    public ApiResponse<TaskResponse> changeStatus(
        Authentication authentication,
        @Parameter(description = "任务 ID", example = "1", required = true)
        @PathVariable Long taskId,
        @Valid @RequestBody ChangeTaskStatusRequest request
    ) {
        return ApiResponse.ok(taskService.changeStatus(authentication.getName(), taskId, request));
    }

    @DeleteMapping("/api/tasks/{taskId}")
    @Operation(summary = "删除任务", description = "项目负责人或管理员可软删除任务。")
    public ApiResponse<Void> deleteTask(
        Authentication authentication,
        @Parameter(description = "任务 ID", example = "1", required = true)
        @PathVariable Long taskId
    ) {
        taskService.deleteTask(authentication.getName(), taskId);
        return ApiResponse.ok();
    }

    @GetMapping("/api/projects/{projectId}/task-labels")
    @Operation(summary = "查询任务标签", description = "返回项目下可使用的任务标签。")
    public ApiResponse<List<TaskLabelResponse>> listLabels(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId
    ) {
        return ApiResponse.ok(taskService.listLabels(authentication.getName(), projectId));
    }

    @PostMapping("/api/projects/{projectId}/task-labels")
    @Operation(summary = "创建任务标签", description = "项目负责人或管理员可创建任务标签。")
    public ApiResponse<TaskLabelResponse> createLabel(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId,
        @Valid @RequestBody CreateTaskLabelRequest request
    ) {
        return ApiResponse.ok(taskService.createLabel(authentication.getName(), projectId, request));
    }

    @GetMapping("/api/tasks/{taskId}/logs")
    @Operation(summary = "查询任务操作日志", description = "返回任务创建、更新、状态流转和删除记录。")
    public ApiResponse<List<TaskLogResponse>> listLogs(
        Authentication authentication,
        @Parameter(description = "任务 ID", example = "1", required = true)
        @PathVariable Long taskId
    ) {
        return ApiResponse.ok(taskService.listLogs(authentication.getName(), taskId));
    }
}
