package com.codecollab.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecollab.modules.project.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}
