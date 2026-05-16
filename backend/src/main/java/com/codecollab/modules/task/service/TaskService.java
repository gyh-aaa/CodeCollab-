package com.codecollab.modules.task.service;

import com.codecollab.modules.project.dto.BoardColumnResponse;
import com.codecollab.modules.task.dto.ChangeTaskStatusRequest;
import com.codecollab.modules.task.dto.CreateTaskLabelRequest;
import com.codecollab.modules.task.dto.CreateTaskRequest;
import com.codecollab.modules.task.dto.TaskLabelResponse;
import com.codecollab.modules.task.dto.TaskLogResponse;
import com.codecollab.modules.task.dto.TaskResponse;
import com.codecollab.modules.task.dto.UpdateTaskRequest;
import java.util.List;

public interface TaskService {

    List<TaskResponse> listTasks(String username, Long projectId, String status, Long assigneeId, String keyword);

    TaskResponse createTask(String username, Long projectId, CreateTaskRequest request);

    TaskResponse getTask(String username, Long taskId);

    TaskResponse updateTask(String username, Long taskId, UpdateTaskRequest request);

    TaskResponse changeStatus(String username, Long taskId, ChangeTaskStatusRequest request);

    void deleteTask(String username, Long taskId);

    List<TaskLabelResponse> listLabels(String username, Long projectId);

    TaskLabelResponse createLabel(String username, Long projectId, CreateTaskLabelRequest request);

    List<TaskLogResponse> listLogs(String username, Long taskId);

    List<BoardColumnResponse> board(String username, Long projectId);
}
