package com.codecollab.modules.project.service.impl;

import com.codecollab.common.BusinessException;
import com.codecollab.common.ErrorCode;
import com.codecollab.modules.project.dto.AddOrganizationMemberRequest;
import com.codecollab.modules.project.dto.AddProjectMemberRequest;
import com.codecollab.modules.project.dto.BoardColumnResponse;
import com.codecollab.modules.project.dto.CreateOrganizationRequest;
import com.codecollab.modules.project.dto.CreateProjectRequest;
import com.codecollab.modules.project.dto.MemberResponse;
import com.codecollab.modules.project.dto.OrganizationResponse;
import com.codecollab.modules.project.dto.ProjectDetailResponse;
import com.codecollab.modules.project.dto.ProjectSummaryResponse;
import com.codecollab.modules.project.dto.TaskCardResponse;
import com.codecollab.modules.project.dto.UpdateOrganizationRequest;
import com.codecollab.modules.project.dto.UpdateProjectRequest;
import com.codecollab.modules.project.service.WorkspaceService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("demo")
public class DemoWorkspaceService implements WorkspaceService {

    private final AtomicLong organizationId = new AtomicLong(2);
    private final AtomicLong projectId = new AtomicLong(4);
    private final AtomicLong memberId = new AtomicLong(10);
    private final Map<String, DemoUser> users = Map.of(
        "admin", new DemoUser(1L, "admin", "系统管理员", "admin@codecollab.local"),
        "pm", new DemoUser(2L, "pm", "项目负责人", "pm@codecollab.local"),
        "member", new DemoUser(3L, "member", "研发成员", "member@codecollab.local")
    );
    private final List<DemoOrganization> organizations = new ArrayList<>();
    private final List<DemoProject> projects = new ArrayList<>();
    private final Map<Long, List<DemoMember>> organizationMembers = new HashMap<>();
    private final Map<Long, List<DemoMember>> projectMembers = new HashMap<>();

    public DemoWorkspaceService() {
        organizations.add(new DemoOrganization(1L, "CodeCollab 产品组", "负责项目协作平台的研发与交付", 1L, "ACTIVE", LocalDateTime.now().minusDays(5)));
        organizationMembers.put(1L, new ArrayList<>(List.of(
            new DemoMember(1L, 1L, "OWNER", LocalDateTime.now().minusDays(5)),
            new DemoMember(2L, 2L, "ADMIN", LocalDateTime.now().minusDays(4)),
            new DemoMember(3L, 3L, "MEMBER", LocalDateTime.now().minusDays(3))
        )));
        projects.add(new DemoProject(1L, 1L, "协作平台一期", "CCP", "登录、权限和基础项目空间", 1L, "ACTIVE", LocalDate.now().minusDays(5), LocalDate.now().plusDays(20), 18, 7));
        projects.add(new DemoProject(2L, 1L, "移动端体验优化", "MOBILE", "优化小屏幕下的核心操作体验", 2L, "ACTIVE", LocalDate.now().minusDays(2), LocalDate.now().plusDays(24), 11, 4));
        projects.add(new DemoProject(3L, 1L, "权限中心重构", "AUTH", "拆分系统权限与业务权限", 1L, "PLANNING", LocalDate.now().plusDays(3), LocalDate.now().plusDays(35), 9, 2));
        projectMembers.put(1L, new ArrayList<>(List.of(
            new DemoMember(4L, 1L, "OWNER", LocalDateTime.now().minusDays(5)),
            new DemoMember(5L, 2L, "MANAGER", LocalDateTime.now().minusDays(4)),
            new DemoMember(6L, 3L, "DEVELOPER", LocalDateTime.now().minusDays(3))
        )));
        projectMembers.put(2L, new ArrayList<>(List.of(
            new DemoMember(7L, 2L, "OWNER", LocalDateTime.now().minusDays(2)),
            new DemoMember(8L, 1L, "MANAGER", LocalDateTime.now().minusDays(2))
        )));
        projectMembers.put(3L, new ArrayList<>(List.of(
            new DemoMember(9L, 1L, "OWNER", LocalDateTime.now().minusDays(1))
        )));
    }

    @Override
    public List<OrganizationResponse> listOrganizations(String username) {
        Long userId = user(username).id();
        return organizations.stream()
            .filter(organization -> memberOf(organizationMembers, organization.id(), userId) != null)
            .sorted(Comparator.comparing(DemoOrganization::id))
            .map(organization -> toOrganizationResponse(organization, userId))
            .toList();
    }

    @Override
    public OrganizationResponse createOrganization(String username, CreateOrganizationRequest request) {
        DemoUser currentUser = user(username);
        DemoOrganization organization = new DemoOrganization(
            organizationId.getAndIncrement(),
            request.name(),
            request.description(),
            currentUser.id(),
            "ACTIVE",
            LocalDateTime.now()
        );
        organizations.add(organization);
        organizationMembers.put(organization.id(), new ArrayList<>(List.of(
            new DemoMember(memberId.getAndIncrement(), currentUser.id(), "OWNER", LocalDateTime.now())
        )));
        return toOrganizationResponse(organization, currentUser.id());
    }

    @Override
    public OrganizationResponse updateOrganization(String username, Long organizationId, UpdateOrganizationRequest request) {
        Long userId = user(username).id();
        requireOrganizationManager(organizationId, userId);
        DemoOrganization organization = organization(organizationId);
        organization.name = request.name();
        organization.description = request.description();
        return toOrganizationResponse(organization, userId);
    }

    @Override
    public List<MemberResponse> listOrganizationMembers(String username, Long organizationId) {
        Long userId = user(username).id();
        requireOrganizationMember(organizationId, userId);
        return organizationMembers.getOrDefault(organizationId, List.of()).stream()
            .map(member -> toMemberResponse(member))
            .toList();
    }

    @Override
    public MemberResponse addOrganizationMember(String username, Long organizationId, AddOrganizationMemberRequest request) {
        Long userId = user(username).id();
        requireOrganizationManager(organizationId, userId);
        DemoUser target = user(request.username());
        List<DemoMember> members = organizationMembers.computeIfAbsent(organizationId, ignored -> new ArrayList<>());
        DemoMember existing = memberOf(organizationMembers, organizationId, target.id());
        if (existing != null) {
            existing.role = request.memberRole();
            return toMemberResponse(existing);
        }
        DemoMember member = new DemoMember(memberId.getAndIncrement(), target.id(), request.memberRole(), LocalDateTime.now());
        members.add(member);
        return toMemberResponse(member);
    }

    @Override
    public List<ProjectSummaryResponse> listProjects(String username, Long organizationId) {
        Long userId = user(username).id();
        return projects.stream()
            .filter(project -> organizationId == null || organizationId.equals(project.organizationId()))
            .filter(project -> memberOf(projectMembers, project.id(), userId) != null)
            .map(project -> toProjectSummary(project, userId))
            .toList();
    }

    @Override
    public ProjectDetailResponse createProject(String username, CreateProjectRequest request) {
        Long userId = user(username).id();
        requireOrganizationManager(request.organizationId(), userId);
        DemoProject project = new DemoProject(
            projectId.getAndIncrement(),
            request.organizationId(),
            request.name(),
            request.code().toUpperCase(),
            request.description(),
            userId,
            "ACTIVE",
            request.startDate(),
            request.endDate(),
            0,
            0
        );
        projects.add(project);
        projectMembers.put(project.id(), new ArrayList<>(List.of(
            new DemoMember(memberId.getAndIncrement(), userId, "OWNER", LocalDateTime.now())
        )));
        return toProjectDetail(project, userId);
    }

    @Override
    public ProjectDetailResponse getProject(String username, Long projectId) {
        Long userId = user(username).id();
        requireProjectMember(projectId, userId);
        return toProjectDetail(project(projectId), userId);
    }

    @Override
    public ProjectDetailResponse updateProject(String username, Long projectId, UpdateProjectRequest request) {
        Long userId = user(username).id();
        requireProjectManager(projectId, userId);
        DemoProject project = project(projectId);
        project.name = request.name();
        project.description = request.description();
        project.startDate = request.startDate();
        project.endDate = request.endDate();
        return toProjectDetail(project, userId);
    }

    @Override
    public ProjectDetailResponse archiveProject(String username, Long projectId) {
        Long userId = user(username).id();
        requireProjectManager(projectId, userId);
        DemoProject project = project(projectId);
        project.status = "ARCHIVED";
        return toProjectDetail(project, userId);
    }

    @Override
    public List<MemberResponse> listProjectMembers(String username, Long projectId) {
        Long userId = user(username).id();
        requireProjectMember(projectId, userId);
        return projectMembers.getOrDefault(projectId, List.of()).stream()
            .map(this::toMemberResponse)
            .toList();
    }

    @Override
    public MemberResponse addProjectMember(String username, Long projectId, AddProjectMemberRequest request) {
        Long userId = user(username).id();
        requireProjectManager(projectId, userId);
        DemoProject project = project(projectId);
        DemoUser target = user(request.username());
        if (memberOf(organizationMembers, project.organizationId(), target.id()) == null) {
            organizationMembers.get(project.organizationId()).add(new DemoMember(memberId.getAndIncrement(), target.id(), "MEMBER", LocalDateTime.now()));
        }
        List<DemoMember> members = projectMembers.computeIfAbsent(projectId, ignored -> new ArrayList<>());
        DemoMember existing = memberOf(projectMembers, projectId, target.id());
        if (existing != null) {
            existing.role = request.projectRole();
            return toMemberResponse(existing);
        }
        DemoMember member = new DemoMember(memberId.getAndIncrement(), target.id(), request.projectRole(), LocalDateTime.now());
        members.add(member);
        return toMemberResponse(member);
    }

    @Override
    public List<BoardColumnResponse> projectBoard(String username, Long projectId) {
        Long userId = user(username).id();
        requireProjectMember(projectId, userId);
        return List.of(
            new BoardColumnResponse("TODO", "待处理", List.of(
                new TaskCardResponse(101L, "设计 RBAC 数据模型", "HIGH", "系统管理员", "2026-05-18", List.of("权限", "后端")),
                new TaskCardResponse(102L, "梳理任务详情交互", "MEDIUM", "项目负责人", "2026-05-20", List.of("前端"))
            )),
            new BoardColumnResponse("IN_PROGRESS", "进行中", List.of(
                new TaskCardResponse(201L, "搭建项目基础工程", "HIGH", "研发成员", "2026-05-13", List.of("工程化"))
            )),
            new BoardColumnResponse("DONE", "已完成", List.of(
                new TaskCardResponse(301L, "确定技术选型", "LOW", "系统管理员", "2026-05-11", List.of("规划"))
            ))
        );
    }

    private DemoUser user(String username) {
        DemoUser user = users.get(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private DemoOrganization organization(Long id) {
        return organizations.stream()
            .filter(organization -> organization.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "组织不存在"));
    }

    private DemoProject project(Long id) {
        return projects.stream()
            .filter(project -> project.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "项目不存在"));
    }

    private void requireOrganizationMember(Long organizationId, Long userId) {
        organization(organizationId);
        if (memberOf(organizationMembers, organizationId, userId) == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "你不是该组织成员");
        }
    }

    private void requireOrganizationManager(Long organizationId, Long userId) {
        DemoMember member = memberOf(organizationMembers, organizationId, userId);
        if (member == null || (!"OWNER".equals(member.role()) && !"ADMIN".equals(member.role()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有组织管理权限");
        }
    }

    private void requireProjectMember(Long projectId, Long userId) {
        project(projectId);
        if (memberOf(projectMembers, projectId, userId) == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "你不是该项目成员");
        }
    }

    private void requireProjectManager(Long projectId, Long userId) {
        DemoMember member = memberOf(projectMembers, projectId, userId);
        if (member == null || (!"OWNER".equals(member.role()) && !"MANAGER".equals(member.role()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有项目管理权限");
        }
    }

    private DemoMember memberOf(Map<Long, List<DemoMember>> members, Long scopeId, Long userId) {
        return members.getOrDefault(scopeId, List.of()).stream()
            .filter(member -> member.userId().equals(userId))
            .findFirst()
            .orElse(null);
    }

    private OrganizationResponse toOrganizationResponse(DemoOrganization organization, Long currentUserId) {
        DemoUser owner = userById(organization.ownerId());
        DemoMember member = memberOf(organizationMembers, organization.id(), currentUserId);
        long projectCount = projects.stream()
            .filter(project -> project.organizationId().equals(organization.id()))
            .count();
        return new OrganizationResponse(
            organization.id(),
            organization.name(),
            organization.description(),
            organization.ownerId(),
            owner.nickname(),
            organization.status(),
            member == null ? null : member.role(),
            organizationMembers.getOrDefault(organization.id(), List.of()).size(),
            projectCount,
            organization.createdAt()
        );
    }

    private ProjectSummaryResponse toProjectSummary(DemoProject project, Long currentUserId) {
        DemoOrganization organization = organization(project.organizationId());
        DemoUser owner = userById(project.ownerId());
        DemoMember member = memberOf(projectMembers, project.id(), currentUserId);
        return new ProjectSummaryResponse(
            project.id(),
            project.organizationId(),
            organization.name(),
            project.name(),
            project.code(),
            project.description(),
            project.status(),
            project.totalTasks(),
            project.completedTasks(),
            owner.nickname(),
            member == null ? null : member.role()
        );
    }

    private ProjectDetailResponse toProjectDetail(DemoProject project, Long currentUserId) {
        DemoOrganization organization = organization(project.organizationId());
        DemoUser owner = userById(project.ownerId());
        DemoMember member = memberOf(projectMembers, project.id(), currentUserId);
        return new ProjectDetailResponse(
            project.id(),
            project.organizationId(),
            organization.name(),
            project.name(),
            project.code(),
            project.description(),
            project.ownerId(),
            owner.nickname(),
            project.status(),
            project.startDate(),
            project.endDate(),
            member == null ? null : member.role(),
            project.totalTasks(),
            project.completedTasks(),
            projectMembers.getOrDefault(project.id(), List.of()).size()
        );
    }

    private MemberResponse toMemberResponse(DemoMember member) {
        DemoUser user = userById(member.userId());
        return new MemberResponse(member.id(), user.id(), user.username(), user.nickname(), user.email(), member.role(), member.joinedAt());
    }

    private DemoUser userById(Long userId) {
        return users.values().stream()
            .filter(user -> user.id().equals(userId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "用户不存在"));
    }

    private record DemoUser(Long id, String username, String nickname, String email) {
    }

    private static class DemoProject {
        private final Long id;
        private final Long organizationId;
        private final String code;
        private final Long ownerId;
        private final int totalTasks;
        private final int completedTasks;
        private String name;
        private String description;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;

        private DemoProject(Long id, Long organizationId, String name, String code, String description, Long ownerId, String status, LocalDate startDate, LocalDate endDate, int totalTasks, int completedTasks) {
            this.id = id;
            this.organizationId = organizationId;
            this.name = name;
            this.code = code;
            this.description = description;
            this.ownerId = ownerId;
            this.status = status;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
        }

        private Long id() {
            return id;
        }

        private Long organizationId() {
            return organizationId;
        }

        private String name() {
            return name;
        }

        private String code() {
            return code;
        }

        private String description() {
            return description;
        }

        private Long ownerId() {
            return ownerId;
        }

        private String status() {
            return status;
        }

        private LocalDate startDate() {
            return startDate;
        }

        private LocalDate endDate() {
            return endDate;
        }

        private int totalTasks() {
            return totalTasks;
        }

        private int completedTasks() {
            return completedTasks;
        }
    }

    private static class DemoOrganization {
        private final Long id;
        private final Long ownerId;
        private final String status;
        private final LocalDateTime createdAt;
        private String name;
        private String description;

        private DemoOrganization(Long id, String name, String description, Long ownerId, String status, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.ownerId = ownerId;
            this.status = status;
            this.createdAt = createdAt;
        }

        private Long id() {
            return id;
        }

        private String name() {
            return name;
        }

        private String description() {
            return description;
        }

        private Long ownerId() {
            return ownerId;
        }

        private String status() {
            return status;
        }

        private LocalDateTime createdAt() {
            return createdAt;
        }
    }

    private static class DemoMember {
        private final Long id;
        private final Long userId;
        private final LocalDateTime joinedAt;
        private String role;

        private DemoMember(Long id, Long userId, String role, LocalDateTime joinedAt) {
            this.id = id;
            this.userId = userId;
            this.role = role;
            this.joinedAt = joinedAt;
        }

        private Long id() {
            return id;
        }

        private Long userId() {
            return userId;
        }

        private String role() {
            return role;
        }

        private LocalDateTime joinedAt() {
            return joinedAt;
        }
    }
}
