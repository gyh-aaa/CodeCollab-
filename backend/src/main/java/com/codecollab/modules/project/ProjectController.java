package com.codecollab.modules.project;

import com.codecollab.common.ApiResponse;
import com.codecollab.modules.project.dto.BoardColumnResponse;
import com.codecollab.modules.project.dto.ProjectSummaryResponse;
import com.codecollab.modules.project.dto.TaskCardResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @GetMapping
    public ApiResponse<List<ProjectSummaryResponse>> listProjects() {
        return ApiResponse.ok(List.of(
            new ProjectSummaryResponse(1L, "协作平台一期", "CCP", "ACTIVE", 18, 7, "系统管理员"),
            new ProjectSummaryResponse(2L, "移动端体验优化", "MOBILE", "ACTIVE", 11, 4, "项目负责人"),
            new ProjectSummaryResponse(3L, "权限中心重构", "AUTH", "PLANNING", 9, 2, "系统管理员")
        ));
    }

    @GetMapping("/{projectId}/board")
    public ApiResponse<List<BoardColumnResponse>> board(@PathVariable Long projectId) {
        return ApiResponse.ok(List.of(
            new BoardColumnResponse("TODO", "待处理", List.of(
                new TaskCardResponse(101L, "设计 RBAC 数据模型", "HIGH", "系统管理员", "2026-05-18", List.of("权限", "后端")),
                new TaskCardResponse(102L, "梳理任务详情交互", "MEDIUM", "项目负责人", "2026-05-20", List.of("前端"))
            )),
            new BoardColumnResponse("IN_PROGRESS", "进行中", List.of(
                new TaskCardResponse(201L, "搭建项目基础工程", "HIGH", "研发成员", "2026-05-13", List.of("工程化")),
                new TaskCardResponse(202L, "统一接口响应格式", "MEDIUM", "系统管理员", "2026-05-14", List.of("后端"))
            )),
            new BoardColumnResponse("DONE", "已完成", List.of(
                new TaskCardResponse(301L, "确定技术选型", "LOW", "系统管理员", "2026-05-11", List.of("规划"))
            ))
        ));
    }
}
