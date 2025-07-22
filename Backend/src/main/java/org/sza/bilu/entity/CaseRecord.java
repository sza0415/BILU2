package org.sza.bilu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 笔录记录实体类
 */
@Data
@TableName("case_record")
public class CaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 笔录名称
     */
    private String recordName;

    /**
     * 询问时间
     */
    private LocalDateTime inquiryTime;

    /**
     * 询问地点
     */
    private String inquiryAddress;

    /**
     * 询问人
     */
    private String inquirerName;

    /**
     * 记录人
     */
    private String assistantName;

    /**
     * 询问人ID
     */
    private String inquiryId;

    /**
     * 笔录内容
     */
    private String textContent;

    /**
     * 内容哈希值
     */
    private String contentHash;

    /**
     * 关联视频文件路径
     */
    private String videoFilePath;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 所属案件ID
     */
    private Long caseId;
} 