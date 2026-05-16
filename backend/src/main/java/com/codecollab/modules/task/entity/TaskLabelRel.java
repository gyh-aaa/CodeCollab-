package com.codecollab.modules.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("task_label_rel")
public class TaskLabelRel {

    private Long taskId;
    private Long labelId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }
}
