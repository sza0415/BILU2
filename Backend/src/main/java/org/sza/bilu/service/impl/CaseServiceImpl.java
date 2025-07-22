package org.sza.bilu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.sza.bilu.entity.CaseEntity;
import org.sza.bilu.mapper.CaseMapper;
import org.sza.bilu.service.CaseService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 案件服务实现类
 */
@Service
public class CaseServiceImpl extends ServiceImpl<CaseMapper, CaseEntity> implements CaseService {

    private static final List<String> DEFAULT_CASE_TYPES = Arrays.asList(
        "刑事案件", "民事案件", "行政案件"
    );
    
    private static final List<String> DEFAULT_STATUS_CHOICES = Arrays.asList(
        "调查中", "审理中", "已结案"
    );

    @Override
    public List<String> getCaseTypes() {
        // 从数据库中获取所有已使用的案件类型
        List<String> dbCaseTypes = this.baseMapper.selectList(null)
                .stream()
                .map(CaseEntity::getCaseType)
                .distinct()
                .collect(Collectors.toList());
        
        // 如果数据库中有案件类型，则返回数据库中的，否则返回默认值
        return dbCaseTypes.isEmpty() ? DEFAULT_CASE_TYPES : dbCaseTypes;
    }

    @Override
    public List<String> getStatusChoices() {
        // 从数据库中获取所有已使用的案件状态
        List<String> dbStatusChoices = this.baseMapper.selectList(null)
                .stream()
                .map(CaseEntity::getStatus)
                .distinct()
                .collect(Collectors.toList());
        
        // 如果数据库中有案件状态，则返回数据库中的，否则返回默认值
        return dbStatusChoices.isEmpty() ? DEFAULT_STATUS_CHOICES : dbStatusChoices;
    }
}