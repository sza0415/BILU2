package org.sza.bilu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sza.bilu.entity.CaseEntity;
import org.sza.bilu.entity.CaseRecord;

import java.util.List;

/**
 * 笔录记录服务接口
 */
public interface CaseRecordService extends IService<CaseRecord> {
    
    /**
     * 计算笔录内容的哈希值
     * @param content 笔录内容
     * @return 哈希值
     */
    String calculateContentHash(String content);
    
    /**
     * 验证笔录内容的哈希值
     * @param content 笔录内容
     * @param hash 哈希值
     * @return 是否匹配
     */
    boolean verifyContentHash(String content, String hash);
    
    /**
     * 生成笔录的Word文档并计算哈希值
     * @param record 笔录记录
     * @param caseInfo 案件信息（可选）
     * @param calculateHash 是否计算哈希值
     * @return 文档字节数组
     */
    byte[] generateWordDocument(CaseRecord record, CaseEntity caseInfo, boolean calculateHash);
    
    /**
     * 将笔录记录打包为ZIP文件
     * @param caseInfo 案件信息
     * @param records 笔录记录列表
     * @return ZIP文件字节数组
     */
    byte[] packageRecordsToZip(CaseEntity caseInfo, List<CaseRecord> records);
}