package org.sza.bilu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.sza.bilu.entity.SecrecyTemplate;

import java.util.List;

/**
 * 保密问答模板服务接口
 */
public interface SecrecyTemplateService extends IService<SecrecyTemplate> {

    /**
     * 分页获取模板列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页数据
     */
    Page<SecrecyTemplate> getTemplatesPage(int page, int size);

    /**
     * 根据类型分页获取模板列表
     *
     * @param templateType 模板类型
     * @param page 页码
     * @param size 每页大小
     * @return 分页数据
     */
    Page<SecrecyTemplate> getTemplatesByType(String templateType, int page, int size);

    /**
     * 根据类型获取模板列表
     *
     * @param templateType 模板类型
     * @return 模板列表
     */
    List<SecrecyTemplate> getTemplatesByType(String templateType);

    /**
     * 获取所有模板类型
     *
     * @return 模板类型列表
     */
    List<String> getAllTemplateTypes();

    /**
     * 从文件导入模板
     *
     * @param file 上传的文件
     * @return 导入数量
     */
    int importFromFile(MultipartFile file) throws Exception;

    /**
     * 从CSV文件导入模板
     *
     * @param file CSV文件
     * @return 导入数量
     */
    int importFromCsv(MultipartFile file) throws Exception;

    /**
     * 导出模板到Excel
     *
     * @param templateType 模板类型（可选）
     * @return Excel文件字节数组
     */
    byte[] exportToExcel(String templateType) throws Exception;
} 