package com.codecollab.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecollab.modules.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("""
        SELECT COUNT(*)
        FROM task
        WHERE project_id = #{projectId}
          AND deleted = 0
        """)
    int countTasksByProjectId(Long projectId);

    @Select("""
        SELECT COUNT(*)
        FROM task
        WHERE project_id = #{projectId}
          AND status = 'DONE'
          AND deleted = 0
        """)
    int countCompletedTasksByProjectId(Long projectId);
}
