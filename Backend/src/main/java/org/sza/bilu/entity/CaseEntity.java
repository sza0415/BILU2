package org.sza.bilu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 案件实体类
 */
@Data
@TableName("`case`")
public class CaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 案件名称
     */
    private String caseName;

    /**
     * 案件编号
     */
    private String caseNumber;

    /**
     * 案件类型
     */
    private String caseType;

    /**
     * 立案日期
     */
    private LocalDate startDate;

    /**
     * 结案日期
     */
    private LocalDate endDate;

    /**
     * 案件状态
     */
    private String status;

    /**
     * 案件描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}