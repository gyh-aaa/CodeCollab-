package com.codecollab.modules.project.dto;

import java.util.List;

public record BoardColumnResponse(
    String key,
    String title,
    List<TaskCardResponse> tasks
) {
}
