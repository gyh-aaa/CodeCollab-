package com.codecollab.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecollab.modules.task.entity.TaskLabel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskLabelMapper extends BaseMapper<TaskLabel> {
}
