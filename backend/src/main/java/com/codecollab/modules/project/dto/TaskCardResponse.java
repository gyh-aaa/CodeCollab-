package com.codecollab.modules.project.dto;

import java.util.List;

public record TaskCardResponse(
    Long id,
    String title,
    String priority,
    String assigneeName,
    String dueDate,
    List<String> labels
) {
}
