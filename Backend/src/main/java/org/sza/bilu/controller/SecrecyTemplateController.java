package org.sza.bilu.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sza.bilu.dto.PageResult;
import org.sza.bilu.dto.R;
import org.sza.bilu.entity.SecrecyTemplate;
import org.sza.bilu.service.SecrecyTemplateService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保密问答模板管理控制器
 */
@Tag(name = "保密模板管理")
@RestController
@RequestMapping("/api/module/secrecy")
@Slf4j
public class SecrecyTemplateController {

    @Autowired
    private SecrecyTemplateService secrecyTemplateService;

    /**
     * 获取保密模板列表（分页）
     */
    @Operation(summary = "获取保密模板列表")
    @GetMapping("")
    public R<PageResult<SecrecyTemplate>> getTemplates(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "templateType", required = false) String templateType) {
        
        try {
            Page<SecrecyTemplate> pageData;
            if (StringUtils.isNotBlank(templateType)) {
                pageData = secrecyTemplateService.getTemplatesByType(templateType, page, size);
            } else {
                pageData = secrecyTemplateService.getTemplatesPage(page, size);
            }
            
            // 使用DTO封装返回结果
            PageResult<SecrecyTemplate> result = PageResult.of(
                pageData.getTotal(),
                pageData.getPages(),
                pageData.getCurrent(),
                pageData.getRecords(),
                secrecyTemplateService.getAllTemplateTypes()
            );
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("获取保密模板列表失败", e);
            return R.fail("获取保密模板列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加保密模板
     */
    @Operation(summary = "添加保密模板")
    @PostMapping("/add")
    public R<String> addTemplate(@RequestBody SecrecyTemplate template) {
        try {
            secrecyTemplateService.save(template);
            return R.ok("添加成功");
        } catch (Exception e) {
            log.error("添加保密模板失败", e);
            return R.fail("添加保密模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加带分类的保密模板
     */
    @Operation(summary = "添加带分类的保密模板")
    @PostMapping("/add_with_category")
    public R<String> addTemplateWithCategory(
            @RequestParam("templateType") String templateType,
            @RequestParam("question") String question,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder) {
        try {
            SecrecyTemplate template = new SecrecyTemplate();
            template.setTemplateType(templateType);
            template.setQuestion(question);
            template.setSortOrder(sortOrder);
            
            secrecyTemplateService.save(template);
            return R.ok("添加成功");
        } catch (Exception e) {
            log.error("添加保密模板失败", e);
            return R.fail("添加保密模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 编辑保密模板
     */
    @Operation(summary = "编辑保密模板")
    @PutMapping("/{id}/edit")
    public R<String> editTemplate(
            @Parameter(description = "模板ID") @PathVariable("id") Long id,
            @RequestBody SecrecyTemplate template) {
        try {
            template.setId(id);
            secrecyTemplateService.updateById(template);
            return R.ok("更新成功");
        } catch (Exception e) {
            log.error("编辑保密模板失败", e);
            return R.fail("编辑保密模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除保密模板
     */
    @Operation(summary = "删除保密模板")
    @DeleteMapping("/delete")
    public R<String> deleteTemplate(@RequestParam("id") Long id) {
        try {
            secrecyTemplateService.removeById(id);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.error("删除保密模板失败", e);
            return R.fail("删除保密模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除保密模板类型（批量删除）
     */
    @Operation(summary = "删除保密模板类型")
    @DeleteMapping("/delete_type")
    public R<String> deleteTemplateType(@RequestParam("templateType") String templateType) {
        try {
            LambdaQueryWrapper<SecrecyTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SecrecyTemplate::getTemplateType, templateType);
            secrecyTemplateService.remove(queryWrapper);
            return R.ok("删除类型成功");
        } catch (Exception e) {
            log.error("删除保密模板类型失败", e);
            return R.fail("删除保密模板类型失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有模板类型
     */
    @Operation(summary = "获取所有模板类型")
    @GetMapping("/types")
    public R<List<String>> getAllTemplateTypes() {
        try {
            List<String> templateTypes = secrecyTemplateService.getAllTemplateTypes();
            return R.ok(templateTypes);
        } catch (Exception e) {
            log.error("获取模板类型失败", e);
            return R.fail("获取模板类型失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有模板（不分页，用于API调用）
     */
    @Operation(summary = "获取所有模板")
    @GetMapping("/get_templates")
    public R<List<SecrecyTemplate>> getAllTemplates(
            @RequestParam(value = "templateType", required = false) String templateType) {
        try {
            List<SecrecyTemplate> templates;
            if (StringUtils.isNotBlank(templateType)) {
                templates = secrecyTemplateService.getTemplatesByType(templateType);
            } else {
                templates = secrecyTemplateService.list();
            }
            return R.ok(templates);
        } catch (Exception e) {
            log.error("获取所有模板失败", e);
            return R.fail("获取所有模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 导入模板
     */
    @Operation(summary = "导入模板")
    @PostMapping("/import")
    public R<Integer> importTemplate(@RequestParam("file") MultipartFile file) {
        try {
            int count = secrecyTemplateService.importFromFile(file);
            return R.ok("成功导入" + count + "条数据", count);
        } catch (Exception e) {
            log.error("导入模板失败", e);
            return R.fail("导入模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 导入CSV模板
     */
    @Operation(summary = "导入CSV模板")
    @PostMapping("/import_csv")
    public R<Integer> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            int count = secrecyTemplateService.importFromCsv(file);
            return R.ok("成功导入" + count + "条数据", count);
        } catch (Exception e) {
            log.error("导入CSV模板失败", e);
            return R.fail("导入CSV模板失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出模板
     */
    @Operation(summary = "导出模板")
    @GetMapping("/export")
    public ResponseEntity<?> exportTemplates(
            @RequestParam(value = "templateType", required = false) String templateType,
            HttpServletResponse response) {
        try {
            byte[] data = secrecyTemplateService.exportToExcel(templateType);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=secrecy_templates.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(data.length)
                    .body(data);
        } catch (Exception e) {
            log.error("导出模板失败", e);
            return ResponseEntity.badRequest().body("导出模板失败：" + e.getMessage());
        }
    }
}