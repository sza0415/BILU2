package org.sza.bilu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.sza.bilu.entity.CaseEntity;

/**
 * 案件Mapper接口
 */
@Mapper
public interface CaseMapper extends BaseMapper<CaseEntity> {
    
    // 可以在此添加自定义的SQL方法
    // 基础的CRUD操作已由BaseMapper提供
    
}