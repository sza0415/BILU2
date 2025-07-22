package org.sza.bilu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sza.bilu.entity.CaseEntity;

import java.util.List;

/**
 * 案件服务接口
 */
public interface CaseService extends IService<CaseEntity> {

    /**
     * 获取所有案件类型
     * @return 案件类型列表
     */
    List<String> getCaseTypes();
    
    /**
     * 获取所有案件状态选项
     * @return 案件状态列表
     */
    List<String> getStatusChoices();
}