package com.codecollab.modules.project.dto;

public record ProjectSummaryResponse(
    Long id,
    String name,
    String code,
    String status,
    int totalTasks,
    int completedTasks,
    String ownerName
) {
}
