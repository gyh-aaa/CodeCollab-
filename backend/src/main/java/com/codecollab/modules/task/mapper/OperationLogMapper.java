package com.codecollab.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecollab.modules.task.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
