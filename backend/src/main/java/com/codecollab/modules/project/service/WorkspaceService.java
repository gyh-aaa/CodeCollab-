package com.codecollab.modules.project.service;

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
import java.util.List;

public interface WorkspaceService {

    List<OrganizationResponse> listOrganizations(String username);

    OrganizationResponse createOrganization(String username, CreateOrganizationRequest request);

    OrganizationResponse updateOrganization(String username, Long organizationId, UpdateOrganizationRequest request);

    List<MemberResponse> listOrganizationMembers(String username, Long organizationId);

    MemberResponse addOrganizationMember(String username, Long organizationId, AddOrganizationMemberRequest request);

    List<ProjectSummaryResponse> listProjects(String username, Long organizationId);

    ProjectDetailResponse createProject(String username, CreateProjectRequest request);

    ProjectDetailResponse getProject(String username, Long projectId);

    ProjectDetailResponse updateProject(String username, Long projectId, UpdateProjectRequest request);

    ProjectDetailResponse archiveProject(String username, Long projectId);

    List<MemberResponse> listProjectMembers(String username, Long projectId);

    MemberResponse addProjectMember(String username, Long projectId, AddProjectMemberRequest request);

    List<BoardColumnResponse> projectBoard(String username, Long projectId);
}
