package com.codecollab.modules.project.controller;

import com.codecollab.common.ApiResponse;
import com.codecollab.modules.project.dto.AddProjectMemberRequest;
import com.codecollab.modules.project.dto.BoardColumnResponse;
import com.codecollab.modules.project.dto.CreateProjectRequest;
import com.codecollab.modules.project.dto.MemberResponse;
import com.codecollab.modules.project.dto.ProjectDetailResponse;
import com.codecollab.modules.project.dto.ProjectSummaryResponse;
import com.codecollab.modules.project.dto.UpdateProjectRequest;
import com.codecollab.modules.project.service.WorkspaceService;
import com.codecollab.modules.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "项目管理", description = "项目列表、项目详情、成员和任务看板接口")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final WorkspaceService workspaceService;
    private final TaskService taskService;

    public ProjectController(WorkspaceService workspaceService, TaskService taskService) {
        this.workspaceService = workspaceService;
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "查询项目列表", description = "返回当前用户可访问的项目摘要数据。")
    public ApiResponse<List<ProjectSummaryResponse>> listProjects(
        Authentication authentication,
        @RequestParam(required = false) Long organizationId
    ) {
        return ApiResponse.ok(workspaceService.listProjects(authentication.getName(), organizationId));
    }

    @PostMapping
    @Operation(summary = "创建项目", description = "在当前用户可管理的组织下创建项目。")
    public ApiResponse<ProjectDetailResponse> createProject(
        Authentication authentication,
        @Valid @RequestBody CreateProjectRequest request
    ) {
        return ApiResponse.ok(workspaceService.createProject(authentication.getName(), request));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "查询项目详情", description = "返回项目基础信息、当前用户角色和任务统计。")
    public ApiResponse<ProjectDetailResponse> getProject(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId
    ) {
        return ApiResponse.ok(workspaceService.getProject(authentication.getName(), projectId));
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目", description = "项目负责人或管理员可更新项目信息。")
    public ApiResponse<ProjectDetailResponse> updateProject(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId,
        @Valid @RequestBody UpdateProjectRequest request
    ) {
        return ApiResponse.ok(workspaceService.updateProject(authentication.getName(), projectId, request));
    }

    @PostMapping("/{projectId}/archive")
    @Operation(summary = "归档项目", description = "项目负责人或管理员可归档项目。")
    public ApiResponse<ProjectDetailResponse> archiveProject(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId
    ) {
        return ApiResponse.ok(workspaceService.archiveProject(authentication.getName(), projectId));
    }

    @GetMapping("/{projectId}/members")
    @Operation(summary = "查询项目成员", description = "返回项目成员和项目角色。")
    public ApiResponse<List<MemberResponse>> listMembers(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId
    ) {
        return ApiResponse.ok(workspaceService.listProjectMembers(authentication.getName(), projectId));
    }

    @PostMapping("/{projectId}/members")
    @Operation(summary = "添加项目成员", description = "项目负责人或管理员可添加项目成员。")
    public ApiResponse<MemberResponse> addMember(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId,
        @Valid @RequestBody AddProjectMemberRequest request
    ) {
        return ApiResponse.ok(workspaceService.addProjectMember(authentication.getName(), projectId, request));
    }

    @GetMapping("/{projectId}/board")
    @Operation(summary = "查询项目任务看板", description = "按任务状态返回看板列和任务卡片数据。")
    public ApiResponse<List<BoardColumnResponse>> board(
        Authentication authentication,
        @Parameter(description = "项目 ID", example = "1", required = true)
        @PathVariable Long projectId
    ) {
        return ApiResponse.ok(taskService.board(authentication.getName(), projectId));
    }
}
