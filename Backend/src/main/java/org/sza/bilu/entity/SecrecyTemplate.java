package org.sza.bilu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 保密问答模板实体类
 */
@Data
@TableName("secrecy_template")
public class SecrecyTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 问题
     */
    private String question;

    /**
     * 排序
     */
    private Integer sortOrder;
} 