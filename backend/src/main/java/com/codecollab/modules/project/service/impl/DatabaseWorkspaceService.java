package com.codecollab.modules.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.codecollab.modules.project.dto.UpdateOrganizationRequest;
import com.codecollab.modules.project.dto.UpdateProjectRequest;
import com.codecollab.modules.project.entity.Organization;
import com.codecollab.modules.project.entity.OrganizationMember;
import com.codecollab.modules.project.entity.Project;
import com.codecollab.modules.project.entity.ProjectMember;
import com.codecollab.modules.project.mapper.OrganizationMapper;
import com.codecollab.modules.project.mapper.OrganizationMemberMapper;
import com.codecollab.modules.project.mapper.ProjectMapper;
import com.codecollab.modules.project.mapper.ProjectMemberMapper;
import com.codecollab.modules.project.service.WorkspaceService;
import com.codecollab.modules.system.entity.SysUser;
import com.codecollab.modules.system.mapper.SysUserMapper;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("mysql")
public class DatabaseWorkspaceService implements WorkspaceService {

    private static final Set<String> ORGANIZATION_ROLES = Set.of("OWNER", "ADMIN", "MEMBER");
    private static final Set<String> PROJECT_ROLES = Set.of("OWNER", "MANAGER", "DEVELOPER", "VIEWER");

    private final OrganizationMapper organizationMapper;
    private final OrganizationMemberMapper organizationMemberMapper;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final SysUserMapper sysUserMapper;

    public DatabaseWorkspaceService(
        OrganizationMapper organizationMapper,
        OrganizationMemberMapper organizationMemberMapper,
        ProjectMapper projectMapper,
        ProjectMemberMapper projectMemberMapper,
        SysUserMapper sysUserMapper
    ) {
        this.organizationMapper = organizationMapper;
        this.organizationMemberMapper = organizationMemberMapper;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public List<OrganizationResponse> listOrganizations(String username) {
        SysUser currentUser = requireUser(username);
        List<OrganizationMember> memberships = organizationMemberMapper.selectList(new LambdaQueryWrapper<OrganizationMember>()
            .eq(OrganizationMember::getUserId, currentUser.getId()));
        if (memberships.isEmpty()) {
            return List.of();
        }
        return memberships.stream()
            .map(member -> organizationMapper.selectById(member.getOrganizationId()))
            .filter(organization -> organization != null && !isDeleted(organization.getDeleted()))
            .sorted(Comparator.comparing(Organization::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(organization -> toOrganizationResponse(organization, currentUser.getId()))
            .toList();
    }

    @Override
    @Transactional
    public OrganizationResponse createOrganization(String username, CreateOrganizationRequest request) {
        SysUser currentUser = requireUser(username);
        Organization organization = new Organization();
        organization.setName(request.name().trim());
        organization.setDescription(request.description());
        organization.setOwnerId(currentUser.getId());
        organization.setStatus("ACTIVE");
        organization.setDeleted(0);
        organizationMapper.insert(organization);

        OrganizationMember member = new OrganizationMember();
        member.setOrganizationId(organization.getId());
        member.setUserId(currentUser.getId());
        member.setMemberRole("OWNER");
        member.setJoinedAt(LocalDateTime.now());
        organizationMemberMapper.insert(member);
        return toOrganizationResponse(organization, currentUser.getId());
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(String username, Long organizationId, UpdateOrganizationRequest request) {
        SysUser currentUser = requireUser(username);
        Organization organization = requireOrganization(organizationId);
        requireOrganizationManager(organizationId, currentUser.getId());
        organization.setName(request.name().trim());
        organization.setDescription(request.description());
        organizationMapper.updateById(organization);
        return toOrganizationResponse(requireOrganization(organizationId), currentUser.getId());
    }

    @Override
    public List<MemberResponse> listOrganizationMembers(String username, Long organizationId) {
        SysUser currentUser = requireUser(username);
        requireOrganizationMember(organizationId, currentUser.getId());
        return organizationMemberMapper.selectList(new LambdaQueryWrapper<OrganizationMember>()
                .eq(OrganizationMember::getOrganizationId, organizationId)
                .orderByAsc(OrganizationMember::getJoinedAt))
            .stream()
            .map(member -> toMemberResponse(member.getId(), member.getUserId(), member.getMemberRole(), member.getJoinedAt()))
            .toList();
    }

    @Override
    @Transactional
    public MemberResponse addOrganizationMember(String username, Long organizationId, AddOrganizationMemberRequest request) {
        SysUser currentUser = requireUser(username);
        requireOrganization(organizationId);
        requireOrganizationManager(organizationId, currentUser.getId());
        SysUser targetUser = requireUser(request.username());
        String role = normalizeRole(request.memberRole(), ORGANIZATION_ROLES);

        OrganizationMember existing = organizationMember(organizationId, targetUser.getId());
        if (existing != null) {
            existing.setMemberRole(role);
            organizationMemberMapper.updateById(existing);
            return toMemberResponse(existing.getId(), targetUser.getId(), role, existing.getJoinedAt());
        }

        OrganizationMember member = new OrganizationMember();
        member.setOrganizationId(organizationId);
        member.setUserId(targetUser.getId());
        member.setMemberRole(role);
        member.setJoinedAt(LocalDateTime.now());
        organizationMemberMapper.insert(member);
        return toMemberResponse(member.getId(), targetUser.getId(), role, member.getJoinedAt());
    }

    @Override
    public List<ProjectSummaryResponse> listProjects(String username, Long organizationId) {
        SysUser currentUser = requireUser(username);
        List<ProjectMember> memberships = projectMemberMapper.selectList(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getUserId, currentUser.getId()));
        if (memberships.isEmpty()) {
            return List.of();
        }
        return memberships.stream()
            .map(member -> projectMapper.selectById(member.getProjectId()))
            .filter(project -> project != null && !isDeleted(project.getDeleted()))
            .filter(project -> organizationId == null || organizationId.equals(project.getOrganizationId()))
            .sorted(Comparator.comparing(Project::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .map(project -> toProjectSummary(project, currentUser.getId()))
            .toList();
    }

    @Override
    @Transactional
    public ProjectDetailResponse createProject(String username, CreateProjectRequest request) {
        SysUser currentUser = requireUser(username);
        Organization organization = requireOrganization(request.organizationId());
        requireOrganizationManager(organization.getId(), currentUser.getId());
        ensureProjectCodeAvailable(organization.getId(), request.code());

        Project project = new Project();
        project.setOrganizationId(organization.getId());
        project.setName(request.name().trim());
        project.setCode(request.code().trim().toUpperCase(Locale.ROOT));
        project.setDescription(request.description());
        project.setOwnerId(currentUser.getId());
        project.setStatus("ACTIVE");
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setDeleted(0);
        projectMapper.insert(project);

        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(currentUser.getId());
        member.setProjectRole("OWNER");
        member.setJoinedAt(LocalDateTime.now());
        projectMemberMapper.insert(member);
        return toProjectDetail(project, currentUser.getId());
    }

    @Override
    public ProjectDetailResponse getProject(String username, Long projectId) {
        SysUser currentUser = requireUser(username);
        Project project = requireProject(projectId);
        requireProjectMember(project.getId(), currentUser.getId());
        return toProjectDetail(project, currentUser.getId());
    }

    @Override
    @Transactional
    public ProjectDetailResponse updateProject(String username, Long projectId, UpdateProjectRequest request) {
        SysUser currentUser = requireUser(username);
        Project project = requireProject(projectId);
        requireProjectManager(project.getId(), currentUser.getId());
        project.setName(request.name().trim());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        projectMapper.updateById(project);
        return toProjectDetail(requireProject(projectId), currentUser.getId());
    }

    @Override
    @Transactional
    public ProjectDetailResponse archiveProject(String username, Long projectId) {
        SysUser currentUser = requireUser(username);
        Project project = requireProject(projectId);
        requireProjectManager(project.getId(), currentUser.getId());
        project.setStatus("ARCHIVED");
        projectMapper.updateById(project);
        return toProjectDetail(requireProject(projectId), currentUser.getId());
    }

    @Override
    public List<MemberResponse> listProjectMembers(String username, Long projectId) {
        SysUser currentUser = requireUser(username);
        requireProject(projectId);
        requireProjectMember(projectId, currentUser.getId());
        return projectMemberMapper.selectList(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)
                .orderByAsc(ProjectMember::getJoinedAt))
            .stream()
            .map(member -> toMemberResponse(member.getId(), member.getUserId(), member.getProjectRole(), member.getJoinedAt()))
            .toList();
    }

    @Override
    @Transactional
    public MemberResponse addProjectMember(String username, Long projectId, AddProjectMemberRequest request) {
        SysUser currentUser = requireUser(username);
        Project project = requireProject(projectId);
        requireProjectManager(project.getId(), currentUser.getId());
        SysUser targetUser = requireUser(request.username());
        String role = normalizeRole(request.projectRole(), PROJECT_ROLES);

        if (organizationMember(project.getOrganizationId(), targetUser.getId()) == null) {
            OrganizationMember organizationMember = new OrganizationMember();
            organizationMember.setOrganizationId(project.getOrganizationId());
            organizationMember.setUserId(targetUser.getId());
            organizationMember.setMemberRole("MEMBER");
            organizationMember.setJoinedAt(LocalDateTime.now());
            organizationMemberMapper.insert(organizationMember);
        }

        ProjectMember existing = projectMember(projectId, targetUser.getId());
        if (existing != null) {
            existing.setProjectRole(role);
            projectMemberMapper.updateById(existing);
            return toMemberResponse(existing.getId(), targetUser.getId(), role, existing.getJoinedAt());
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(targetUser.getId());
        member.setProjectRole(role);
        member.setJoinedAt(LocalDateTime.now());
        projectMemberMapper.insert(member);
        return toMemberResponse(member.getId(), targetUser.getId(), role, member.getJoinedAt());
    }

    @Override
    public List<BoardColumnResponse> projectBoard(String username, Long projectId) {
        SysUser currentUser = requireUser(username);
        requireProject(projectId);
        requireProjectMember(projectId, currentUser.getId());
        return List.of(
            new BoardColumnResponse("TODO", "待处理", List.of()),
            new BoardColumnResponse("IN_PROGRESS", "进行中", List.of()),
            new BoardColumnResponse("DONE", "已完成", List.of())
        );
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
        if (user == null || isDeleted(user.getDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private Organization requireOrganization(Long organizationId) {
        Organization organization = organizationMapper.selectById(organizationId);
        if (organization == null || isDeleted(organization.getDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "组织不存在");
        }
        return organization;
    }

    private Project requireProject(Long projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null || isDeleted(project.getDeleted())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "项目不存在");
        }
        return project;
    }

    private OrganizationMember organizationMember(Long organizationId, Long userId) {
        return organizationMemberMapper.selectOne(new LambdaQueryWrapper<OrganizationMember>()
            .eq(OrganizationMember::getOrganizationId, organizationId)
            .eq(OrganizationMember::getUserId, userId)
            .last("LIMIT 1"));
    }

    private ProjectMember projectMember(Long projectId, Long userId) {
        return projectMemberMapper.selectOne(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, projectId)
            .eq(ProjectMember::getUserId, userId)
            .last("LIMIT 1"));
    }

    private void requireOrganizationMember(Long organizationId, Long userId) {
        requireOrganization(organizationId);
        if (organizationMember(organizationId, userId) == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "你不是该组织成员");
        }
    }

    private void requireOrganizationManager(Long organizationId, Long userId) {
        OrganizationMember member = organizationMember(organizationId, userId);
        if (member == null || (!"OWNER".equals(member.getMemberRole()) && !"ADMIN".equals(member.getMemberRole()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有组织管理权限");
        }
    }

    private void requireProjectMember(Long projectId, Long userId) {
        if (projectMember(projectId, userId) == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "你不是该项目成员");
        }
    }

    private void requireProjectManager(Long projectId, Long userId) {
        ProjectMember member = projectMember(projectId, userId);
        if (member == null || (!"OWNER".equals(member.getProjectRole()) && !"MANAGER".equals(member.getProjectRole()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有项目管理权限");
        }
    }

    private void ensureProjectCodeAvailable(Long organizationId, String code) {
        Project existing = projectMapper.selectOne(new LambdaQueryWrapper<Project>()
            .eq(Project::getOrganizationId, organizationId)
            .eq(Project::getCode, code.trim().toUpperCase(Locale.ROOT))
            .eq(Project::getDeleted, 0)
            .last("LIMIT 1"));
        if (existing != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "项目编码已存在");
        }
    }

    private OrganizationResponse toOrganizationResponse(Organization organization, Long currentUserId) {
        SysUser owner = requireUser(organization.getOwnerId());
        OrganizationMember currentMember = organizationMember(organization.getId(), currentUserId);
        long memberCount = organizationMemberMapper.selectCount(new LambdaQueryWrapper<OrganizationMember>()
            .eq(OrganizationMember::getOrganizationId, organization.getId()));
        long projectCount = projectMapper.selectCount(new LambdaQueryWrapper<Project>()
            .eq(Project::getOrganizationId, organization.getId())
            .eq(Project::getDeleted, 0));
        return new OrganizationResponse(
            organization.getId(),
            organization.getName(),
            organization.getDescription(),
            organization.getOwnerId(),
            owner.getNickname(),
            organization.getStatus(),
            currentMember == null ? null : currentMember.getMemberRole(),
            memberCount,
            projectCount,
            organization.getCreatedAt()
        );
    }

    private ProjectSummaryResponse toProjectSummary(Project project, Long currentUserId) {
        Organization organization = requireOrganization(project.getOrganizationId());
        SysUser owner = requireUser(project.getOwnerId());
        ProjectMember currentMember = projectMember(project.getId(), currentUserId);
        return new ProjectSummaryResponse(
            project.getId(),
            project.getOrganizationId(),
            organization.getName(),
            project.getName(),
            project.getCode(),
            project.getDescription(),
            project.getStatus(),
            projectMapper.countTasksByProjectId(project.getId()),
            projectMapper.countCompletedTasksByProjectId(project.getId()),
            owner.getNickname(),
            currentMember == null ? null : currentMember.getProjectRole()
        );
    }

    private ProjectDetailResponse toProjectDetail(Project project, Long currentUserId) {
        Organization organization = requireOrganization(project.getOrganizationId());
        SysUser owner = requireUser(project.getOwnerId());
        ProjectMember currentMember = projectMember(project.getId(), currentUserId);
        long memberCount = projectMemberMapper.selectCount(new LambdaQueryWrapper<ProjectMember>()
            .eq(ProjectMember::getProjectId, project.getId()));
        return new ProjectDetailResponse(
            project.getId(),
            project.getOrganizationId(),
            organization.getName(),
            project.getName(),
            project.getCode(),
            project.getDescription(),
            project.getOwnerId(),
            owner.getNickname(),
            project.getStatus(),
            project.getStartDate(),
            project.getEndDate(),
            currentMember == null ? null : currentMember.getProjectRole(),
            projectMapper.countTasksByProjectId(project.getId()),
            projectMapper.countCompletedTasksByProjectId(project.getId()),
            memberCount
        );
    }

    private MemberResponse toMemberResponse(Long id, Long userId, String role, LocalDateTime joinedAt) {
        SysUser user = requireUser(userId);
        return new MemberResponse(id, user.getId(), user.getUsername(), user.getNickname(), user.getEmail(), role, joinedAt);
    }

    private String normalizeRole(String role, Set<String> allowedRoles) {
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if (!allowedRoles.contains(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不支持的成员角色");
        }
        return normalized;
    }

    private boolean isDeleted(Integer deleted) {
        return Integer.valueOf(1).equals(deleted);
    }
}
