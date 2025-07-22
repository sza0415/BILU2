package org.sza.bilu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sza.bilu.dto.PageResult;
import org.sza.bilu.dto.R;
import org.sza.bilu.entity.CaseEntity;
import org.sza.bilu.entity.CaseRecord;
import org.sza.bilu.service.CaseRecordService;
import org.sza.bilu.service.CaseService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 笔录记录控制器
 */
@Tag(name = "笔录记录管理")
@RestController
@RequestMapping("/api/record")
@Slf4j
public class CaseRecordController {

    @Autowired
    private CaseRecordService caseRecordService;
    
    @Autowired
    private CaseService caseService;
    
    /**
     * 验证笔录哈希值（防篡改验证）
     */
    @Operation(summary = "验证笔录哈希值")
    @PostMapping(value = "/verify/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Map<String, Object>> verifyRecordHash(
            @PathVariable("id") Long id,
            @Parameter(description = "Word文档") @RequestParam("file") MultipartFile file) {
        try {
            // 获取笔录记录
            CaseRecord record = caseRecordService.getById(id);
            if (record == null) {
                return R.fail("笔录不存在");
            }
            
            // 验证笔录哈希值
            if (record.getContentHash() == null) {
                return R.fail("笔录未进行防篡改保护");
            }
            
            // 读取上传的文档内容
            String documentContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            // 验证哈希值
            boolean isValid = caseRecordService.verifyContentHash(documentContent, record.getContentHash());
            
            // 返回验证结果
            Map<String, Object> result = new HashMap<>();
            result.put("recordId", record.getId());
            result.put("recordName", record.getRecordName());
            result.put("inquiryTime", record.getInquiryTime());
            result.put("verified", isValid);
            
            if (isValid) {
                return R.ok("验证成功！文档未被篡改。", result);
            } else {
                return R.fail("验证失败！文档可能已被篡改。", 400, result);
            }
        } catch (IOException e) {
            log.error("验证笔录哈希值失败", e);
            return R.fail("文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("验证笔录哈希值失败", e);
            return R.fail("验证笔录哈希值失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取笔录详情
     */
    @Operation(summary = "获取笔录详情")
    @GetMapping("/{id}")
    public R<Map<String, Object>> getRecord(@PathVariable("id") Long id) {
        try {
            CaseRecord record = caseRecordService.getById(id);
            if (record == null) {
                return R.fail("记录不存在");
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("record_id", record.getId());
            responseData.put("record_name", record.getRecordName());
            responseData.put("inquiry_time", record.getInquiryTime());
            responseData.put("inquiry_address", record.getInquiryAddress());
            responseData.put("assistant_name", record.getAssistantName());
            responseData.put("inquirer_name", record.getInquirerName());
            responseData.put("inquiry_id", record.getInquiryId());
            responseData.put("text_content", record.getTextContent());
            responseData.put("video_file_path", record.getVideoFilePath());
            responseData.put("created_at", record.getCreatedAt());
            // 添加案件信息（如果存在）
            if (record.getCaseId() != null) {
                CaseEntity caseInfo = caseService.getById(record.getCaseId());
                if (caseInfo != null) {
                    Map<String, Object> caseData = new HashMap<>();
                    caseData.put("case_id", caseInfo.getId());
                    caseData.put("case_name", caseInfo.getCaseName());
                    caseData.put("case_number", caseInfo.getCaseNumber());
                    caseData.put("case_type", caseInfo.getCaseType());
                    caseData.put("status", caseInfo.getStatus());
                    responseData.put("case", caseData);
                }
            }
            
            return R.ok(responseData);
        } catch (Exception e) {
            log.error("获取笔录详情失败", e);
            return R.fail("获取笔录详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有笔录列表
     */
    @Operation(summary = "获取所有笔录列表")
    @GetMapping("/list")
    public R<List<CaseRecord>> getAllRecords() {
        try {
            // 获取所有笔录，按询问时间倒序排列
            LambdaQueryWrapper<CaseRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(CaseRecord::getInquiryTime);
            List<CaseRecord> records = caseRecordService.list(queryWrapper);
            
            return R.ok(records);
        } catch (Exception e) {
            log.error("获取所有笔录列表失败", e);
            return R.fail("获取所有笔录列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索笔录记录
     */
    @Operation(summary = "搜索笔录记录")
    @GetMapping("/search")
    public R<PageResult<CaseRecord>> searchRecords(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "assistant", required = false) String assistant,
            @RequestParam(value = "inquirer", required = false) String inquirer) {
        
        try {
            // 构建基本查询条件
            LambdaQueryWrapper<CaseRecord> queryWrapper = new LambdaQueryWrapper<>();
            
            // 添加关键词搜索条件
            if (query != null && !query.isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like(CaseRecord::getRecordName, query)
                    .or()
                    .like(CaseRecord::getInquiryAddress, query)
                    .or()
                    .like(CaseRecord::getInquirerName, query)
                    .or()
                    .like(CaseRecord::getAssistantName, query)
                    .or()
                    .like(CaseRecord::getInquiryId, query)
                    .or()
                    .like(CaseRecord::getTextContent, query)
                );
            }
            
            // 添加时间范围条件
            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                queryWrapper.ge(CaseRecord::getInquiryTime, startDateTime);
            }
            if (endDate != null) {
                LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
                queryWrapper.lt(CaseRecord::getInquiryTime, endDateTime);
            }
            
            // 添加地点条件
            if (location != null && !location.isEmpty()) {
                queryWrapper.like(CaseRecord::getInquiryAddress, location);
            }
            
            // 添加询问员条件
            if (assistant != null && !assistant.isEmpty()) {
                queryWrapper.like(CaseRecord::getAssistantName, assistant);
            }
            
            // 添加被询问人条件
            if (inquirer != null && !inquirer.isEmpty()) {
                queryWrapper.like(CaseRecord::getInquirerName, inquirer);
            }
            
            // 执行分页查询
            Page<CaseRecord> pageParam = new Page<>(page, size);
            Page<CaseRecord> pageData = caseRecordService.page(pageParam, queryWrapper);
            
            // 使用DTO封装返回结果
            PageResult<CaseRecord> result = PageResult.of(
                pageData.getTotal(),
                pageData.getPages(),
                pageData.getCurrent(),
                pageData.getRecords()
            );
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("搜索笔录记录失败", e);
            return R.fail("搜索笔录记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 编辑笔录
     */
    @Operation(summary = "编辑笔录")
    @PutMapping("/{id}")
    public R<String> editRecord(
            @PathVariable("id") Long id,
            @RequestBody CaseRecord record) {
        try {
            // 检查笔录是否存在
            CaseRecord existingRecord = caseRecordService.getById(id);
            if (existingRecord == null) {
                return R.fail("笔录不存在");
            }
            
            // 设置ID确保更新正确的记录
            record.setId(id);
            
            // 如果记录已加密，更新后需要清除哈希值
            if (existingRecord.getContentHash() != null) {
                record.setContentHash(null);
            }
            
            // 更新笔录信息
            boolean success = caseRecordService.updateById(record);
            
            if (success) {
                return R.ok("笔录更新成功");
            } else {
                return R.fail("笔录更新失败");
            }
        } catch (Exception e) {
            log.error("编辑笔录失败", e);
            return R.fail("编辑笔录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除笔录
     */
    @Operation(summary = "删除笔录")
    @DeleteMapping("/{id}")
    public R<String> deleteRecord(@PathVariable("id") Long id) {
        try {
            // 检查笔录是否存在
            CaseRecord existingRecord = caseRecordService.getById(id);
            if (existingRecord == null) {
                return R.fail("笔录不存在");
            }
            
            String recordName = existingRecord.getRecordName();
            
            // 删除笔录
            boolean success = caseRecordService.removeById(id);
            
            if (success) {
                return R.ok(String.format("笔录 '%s' 已成功删除", recordName));
            } else {
                return R.fail("删除笔录失败");
            }
        } catch (Exception e) {
            log.error("删除笔录失败", e);
            return R.fail("删除笔录失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存笔录内容
     */
    @Operation(summary = "保存笔录内容")
    @PutMapping("/{id}/content")
    public R<String> saveRecordContent(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> requestBody) {
        try {
            String content = requestBody.get("content");
            
            if (content == null) {
                return R.fail("缺少content参数");
            }
            
            // 检查笔录是否存在
            CaseRecord existingRecord = caseRecordService.getById(id);
            if (existingRecord == null) {
                return R.fail("笔录不存在");
            }
            
            // 更新笔录内容
            existingRecord.setTextContent(content);
            
            // 如果记录已加密，更新后需要清除哈希值
            if (existingRecord.getContentHash() != null) {
                existingRecord.setContentHash(null);
            }
            
            boolean success = caseRecordService.updateById(existingRecord);
            
            if (success) {
                return R.ok("笔录内容保存成功");
            } else {
                return R.fail("笔录内容保存失败");
            }
        } catch (Exception e) {
            log.error("保存笔录内容失败", e);
            return R.fail("保存笔录内容失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出笔录为Word文档
     */
    @Operation(summary = "导出笔录")
    @GetMapping("/export/{id}")
    public ResponseEntity<Resource> exportRecord(@PathVariable("id") Long id) {
        try {
            // 获取笔录记录
            CaseRecord record = caseRecordService.getById(id);
            if (record == null) {
                throw new RuntimeException("笔录不存在");
            }
            
            // 获取关联的案件信息
            CaseEntity caseInfo = null;
            if (record.getCaseId() != null) {
                caseInfo = caseService.getById(record.getCaseId());
            }
            
            // 生成Word文档并计算哈希值
            byte[] documentBytes = caseRecordService.generateWordDocument(record, caseInfo, true);
            
            // 创建文件名
            String filename = String.format("%s_%s.docx", 
                    record.getRecordName(), 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            // 对中文文件名进行URL编码
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
            
            // 创建HTTP响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            
            // 创建响应体
            ByteArrayResource resource = new ByteArrayResource(documentBytes);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(documentBytes.length)
                    .body(resource);
        } catch (Exception e) {
            log.error("导出笔录失败", e);
            throw new RuntimeException("导出笔录失败：" + e.getMessage());
        }
    }
}