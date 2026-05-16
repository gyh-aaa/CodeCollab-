package com.codecollab.modules.project.controller;

import com.codecollab.common.ApiResponse;
import com.codecollab.modules.project.dto.AddOrganizationMemberRequest;
import com.codecollab.modules.project.dto.CreateOrganizationRequest;
import com.codecollab.modules.project.dto.MemberResponse;
import com.codecollab.modules.project.dto.OrganizationResponse;
import com.codecollab.modules.project.dto.UpdateOrganizationRequest;
import com.codecollab.modules.project.service.WorkspaceService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "组织管理", description = "组织列表、组织成员和组织级权限接口")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {

    private final WorkspaceService workspaceService;

    public OrganizationController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    @Operation(summary = "查询组织列表", description = "返回当前用户加入的组织。")
    public ApiResponse<List<OrganizationResponse>> listOrganizations(Authentication authentication) {
        return ApiResponse.ok(workspaceService.listOrganizations(authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "创建组织", description = "当前用户创建组织后自动成为组织 OWNER。")
    public ApiResponse<OrganizationResponse> createOrganization(
        Authentication authentication,
        @Valid @RequestBody CreateOrganizationRequest request
    ) {
        return ApiResponse.ok(workspaceService.createOrganization(authentication.getName(), request));
    }

    @PutMapping("/{organizationId}")
    @Operation(summary = "更新组织", description = "组织 OWNER 或 ADMIN 可更新组织信息。")
    public ApiResponse<OrganizationResponse> updateOrganization(
        Authentication authentication,
        @Parameter(description = "组织 ID", example = "1", required = true)
        @PathVariable Long organizationId,
        @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        return ApiResponse.ok(workspaceService.updateOrganization(authentication.getName(), organizationId, request));
    }

    @GetMapping("/{organizationId}/members")
    @Operation(summary = "查询组织成员", description = "返回组织成员和组织角色。")
    public ApiResponse<List<MemberResponse>> listMembers(
        Authentication authentication,
        @Parameter(description = "组织 ID", example = "1", required = true)
        @PathVariable Long organizationId
    ) {
        return ApiResponse.ok(workspaceService.listOrganizationMembers(authentication.getName(), organizationId));
    }

    @PostMapping("/{organizationId}/members")
    @Operation(summary = "添加组织成员", description = "组织 OWNER 或 ADMIN 可添加成员。")
    public ApiResponse<MemberResponse> addMember(
        Authentication authentication,
        @Parameter(description = "组织 ID", example = "1", required = true)
        @PathVariable Long organizationId,
        @Valid @RequestBody AddOrganizationMemberRequest request
    ) {
        return ApiResponse.ok(workspaceService.addOrganizationMember(authentication.getName(), organizationId, request));
    }
}
