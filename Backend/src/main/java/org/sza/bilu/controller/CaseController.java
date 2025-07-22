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
import org.sza.bilu.dto.PageResult;
import org.sza.bilu.dto.R;
import org.sza.bilu.entity.CaseEntity;
import org.sza.bilu.entity.CaseRecord;
import org.sza.bilu.service.CaseRecordService;
import org.sza.bilu.service.CaseService;

import java.util.HashMap;
import java.util.Map;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 案件管理控制器
 */
@Tag(name = "案件管理")
@RestController
@RequestMapping("/api/case")
@Slf4j
public class CaseController {

    @Autowired
    private CaseService caseService;
    
    @Autowired
    private CaseRecordService caseRecordService;
    
    /**
     * 获取案件列表
     */
    @Operation(summary = "获取案件列表")
    @GetMapping("/list")
    public R<List<CaseEntity>> allCases() {
        try {
            // 获取所有案件，按创建时间倒序排列
            LambdaQueryWrapper<CaseEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(CaseEntity::getCreatedAt);
            List<CaseEntity> cases = caseService.list(queryWrapper);
            
            return R.ok(cases);
        } catch (Exception e) {
            log.error("获取案件列表失败", e);
            return R.fail("获取案件列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取案件详情
     */
    @Operation(summary = "获取案件详情")
    @GetMapping("/{id}")
    public R<Map<String, Object>> getCase(@PathVariable("id") Long id) {
        try {
            // 获取案件信息
            CaseEntity caseInfo = caseService.getById(id);
            if (caseInfo == null) {
                return R.fail("案件不存在");
            }
            
            // 获取案件相关的所有笔录，按询问时间倒序排列
            LambdaQueryWrapper<CaseRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.eq(CaseRecord::getCaseId, id)
                            .orderByDesc(CaseRecord::getInquiryTime);
            List<CaseRecord> records = caseRecordService.list(recordQueryWrapper);
            
            Map<String, Object> result = new HashMap<>();
            result.put("case", caseInfo);
            result.put("records", records);
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("获取案件详情失败", e);
            return R.fail("获取案件详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索案件
     */
    @Operation(summary = "搜索案件")
    @GetMapping("/search")
    public R<PageResult<CaseEntity>> searchCases(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "caseType", required = false) String caseType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDateFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDateFrom,
            @RequestParam(value = "startDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDateTo,
            @RequestParam(value = "createdAtFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAtFrom,
            @RequestParam(value = "createdAtTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAtTo) {
        
        try {
            // 构建查询条件
            LambdaQueryWrapper<CaseEntity> queryWrapper = new LambdaQueryWrapper<>();
            
            // 添加关键词搜索条件
            if (query != null && !query.isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like(CaseEntity::getCaseName, query)
                    .or()
                    .like(CaseEntity::getCaseNumber, query)
                    .or()
                    .like(CaseEntity::getCaseType, query)
                    .or()
                    .like(CaseEntity::getStatus, query)
                    .or()
                    .like(CaseEntity::getDescription, query)
                );
            }
            
            // 添加案件类型条件
            if (caseType != null && !caseType.isEmpty()) {
                queryWrapper.like(CaseEntity::getCaseType, caseType);
            }
            
            // 添加案件状态条件
            if (status != null && !status.isEmpty()) {
                queryWrapper.eq(CaseEntity::getStatus, status);
            }
            
            // 添加立案日期范围条件
            if (startDateFrom != null) {
                queryWrapper.ge(CaseEntity::getStartDate, startDateFrom);
            }
            if (startDateTo != null) {
                queryWrapper.le(CaseEntity::getStartDate, startDateTo);
            }
            
            // 添加创建时间范围条件
            if (createdAtFrom != null) {
                LocalDateTime fromDateTime = createdAtFrom.atStartOfDay();
                queryWrapper.ge(CaseEntity::getCreatedAt, fromDateTime);
            }
            if (createdAtTo != null) {
                LocalDateTime toDateTime = createdAtTo.plusDays(1).atStartOfDay();
                queryWrapper.lt(CaseEntity::getCreatedAt, toDateTime);
            }
            
            // 按创建时间倒序排列
            queryWrapper.orderByDesc(CaseEntity::getCreatedAt);
            
            // 执行分页查询
            Page<CaseEntity> pageParam = new Page<>(page, size);
            Page<CaseEntity> pageData = caseService.page(pageParam, queryWrapper);
            
            // 获取案件类型和状态选项（用于前端筛选）
            List<String> caseTypes = caseService.getCaseTypes();
            List<String> statusChoices = caseService.getStatusChoices();
            
            // 构建额外数据
            Map<String, Object> extra = new HashMap<>();
            extra.put("caseTypes", caseTypes);
            extra.put("statusChoices", statusChoices);
            
            // 使用DTO封装返回结果
            PageResult<CaseEntity> result = PageResult.of(
                pageData.getTotal(),
                pageData.getPages(),
                pageData.getCurrent(),
                pageData.getSize(), // 添加每页记录数
                pageData.getRecords(),
                extra
            );
            
            return R.ok(result);
        } catch (Exception e) {
            log.error("搜索案件失败", e);
            return R.fail("搜索案件失败：" + e.getMessage());
        }
    }
    
    /**
     * 编辑案件
     */
    @Operation(summary = "编辑案件")
    @PutMapping("/{id}")
    public R<String> editCase(
            @PathVariable("id") Long id,
            @RequestBody CaseEntity caseInfo) {
        try {
            // 检查案件是否存在
            CaseEntity existingCase = caseService.getById(id);
            if (existingCase == null) {
                return R.fail("案件不存在");
            }
            
            // 设置ID确保更新正确的记录
            caseInfo.setId(id);
            
            // 更新案件信息
            boolean success = caseService.updateById(caseInfo);
            
            if (success) {
                return R.ok("案件信息更新成功");
            } else {
                return R.fail("案件信息更新失败");
            }
        } catch (Exception e) {
            log.error("编辑案件失败", e);
            return R.fail("编辑案件失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除案件
     */
    @Operation(summary = "删除案件")
    @DeleteMapping("/{id}")
    public R<String> deleteCase(@PathVariable("id") Long id) {
        try {
            // 检查案件是否存在
            CaseEntity existingCase = caseService.getById(id);
            if (existingCase == null) {
                return R.fail("案件不存在");
            }
            
            // 获取关联的笔录数量
            LambdaQueryWrapper<CaseRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.eq(CaseRecord::getCaseId, id);
            long recordCount = caseRecordService.count(recordQueryWrapper);
            
            // 删除案件（关联的笔录由外键级联删除）
            boolean success = caseService.removeById(id);
            
            if (success) {
                if (recordCount > 0) {
                    return R.ok(String.format("案件 '%s' 及其关联的 %d 条笔录记录已成功删除", existingCase.getCaseName(), recordCount));
                } else {
                    return R.ok(String.format("案件 '%s' 已成功删除", existingCase.getCaseName()));
                }
            } else {
                return R.fail("删除案件失败");
            }
        } catch (Exception e) {
            log.error("删除案件失败", e);
            return R.fail("删除案件失败：" + e.getMessage());
        }
    }
    
    /**
     * 添加案件
     */
    @Operation(summary = "添加案件")
    @PostMapping("/add")
    public R<CaseEntity> addCase(@RequestBody CaseEntity caseInfo) {
        try {
            // 验证必填字段
            if (caseInfo.getCaseName() == null || caseInfo.getCaseName().isEmpty() ||
                caseInfo.getCaseType() == null || caseInfo.getCaseType().isEmpty() ||
                caseInfo.getStatus() == null || caseInfo.getStatus().isEmpty()) {
                return R.fail("案件名称、案件类型和案件状态为必填项");
            }
            
            // 如果没有提供案件编号，自动生成一个
            if (caseInfo.getCaseNumber() == null || caseInfo.getCaseNumber().isEmpty()) {
                String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                caseInfo.setCaseNumber(String.format("CASE-%s-%s", currentDate, uuid));
            }
            
            // 如果没有提供开始日期，使用当前日期
            if (caseInfo.getStartDate() == null) {
                caseInfo.setStartDate(LocalDate.now());
            }
            
            // 设置创建时间
            caseInfo.setCreatedAt(LocalDateTime.now());
            
            // 保存案件
            boolean success = caseService.save(caseInfo);
            
            if (success) {
                return R.ok("案件添加成功", caseInfo);
            } else {
                return R.fail("案件添加失败");
            }
        } catch (Exception e) {
            log.error("添加案件失败", e);
            return R.fail("添加案件失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取案件类型列表
     */
    @Operation(summary = "获取案件类型列表")
    @GetMapping("/types")
    public R<List<String>> getCaseTypes() {
        try {
            List<String> caseTypes = caseService.getCaseTypes();
            return R.ok(caseTypes);
        } catch (Exception e) {
            log.error("获取案件类型列表失败", e);
            return R.fail("获取案件类型列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取案件状态选项
     */
    @Operation(summary = "获取案件状态选项")
    @GetMapping("/statuses")
    public R<List<String>> getStatusChoices() {
        try {
            List<String> statusChoices = caseService.getStatusChoices();
            return R.ok(statusChoices);
        } catch (Exception e) {
            log.error("获取案件状态选项失败", e);
            return R.fail("获取案件状态选项失败：" + e.getMessage());
        }
    }
    
    /**
     * 打包案件下的所有笔录为ZIP文件
     */
    @Operation(summary = "打包案件笔录")
    @GetMapping("/package/{id}")
    public ResponseEntity<Resource> packageCase(@PathVariable("id") Long id) {
        try {
            // 检查案件是否存在
            CaseEntity caseInfo = caseService.getById(id);
            if (caseInfo == null) {
                throw new RuntimeException("案件不存在");
            }
            
            // 获取该案件下的所有笔录
            LambdaQueryWrapper<CaseRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CaseRecord::getCaseId, id).orderByAsc(CaseRecord::getCreatedAt);
            List<CaseRecord> records = caseRecordService.list(queryWrapper);
            
            if (records.isEmpty()) {
                throw new RuntimeException("该案件下没有笔录可以打包");
            }
            
            // 生成ZIP文件
            byte[] zipBytes = caseRecordService.packageRecordsToZip(caseInfo, records);
            
            // 创建文件名
            String filename = String.format("%s_%s_%s.zip",
                    caseInfo.getCaseName(),
                    caseInfo.getCaseNumber(),
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
            ByteArrayResource resource = new ByteArrayResource(zipBytes);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipBytes.length)
                    .body(resource);
        } catch (Exception e) {
            log.error("打包案件笔录失败", e);
            throw new RuntimeException("打包案件笔录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取案件下的所有笔录，供用户选择打包
     */
    @Operation(summary = "获取案件笔录供选择")
    @GetMapping("/{id}/records")
    public R<List<CaseRecord>> getRecordsForPackage(@PathVariable("id") Long id) {
        try {
            // 检查案件是否存在
            CaseEntity caseInfo = caseService.getById(id);
            if (caseInfo == null) {
                return R.fail("案件不存在");
            }
            
            // 获取该案件下的所有笔录
            LambdaQueryWrapper<CaseRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CaseRecord::getCaseId, id).orderByAsc(CaseRecord::getCreatedAt);
            List<CaseRecord> records = caseRecordService.list(queryWrapper);
            
            return R.ok(records);
        } catch (Exception e) {
            log.error("获取案件笔录失败", e);
            return R.fail("获取案件笔录失败：" + e.getMessage());
        }
    }
    
    /**
     * 打包选定的笔录
     */
    @Operation(summary = "打包选定笔录")
    @PostMapping("/{id}/package-selected")
    public ResponseEntity<Resource> packageSelectedRecords(
            @PathVariable("id") Long id,
            @RequestBody List<Long> recordIds) {
        try {
            // 检查案件是否存在
            CaseEntity caseInfo = caseService.getById(id);
            if (caseInfo == null) {
                throw new RuntimeException("案件不存在");
            }
            
            if (recordIds == null || recordIds.isEmpty()) {
                throw new RuntimeException("请至少选择一个笔录进行打包");
            }
            
            // 获取选定的笔录
            List<CaseRecord> records = caseRecordService.listByIds(recordIds);
            
            if (records.isEmpty()) {
                throw new RuntimeException("选择的笔录不存在");
            }
            
            // 检查是否所有选择的笔录都属于该案件
            for (CaseRecord record : records) {
                if (!id.equals(record.getCaseId())) {
                    throw new RuntimeException("所选笔录不全部属于该案件");
                }
            }
            
            // 生成ZIP文件
            byte[] zipBytes = caseRecordService.packageRecordsToZip(caseInfo, records);
            
            // 创建文件名
            String filename = String.format("%s_%s_选择笔录_%s.zip",
                    caseInfo.getCaseName(),
                    caseInfo.getCaseNumber(),
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
            ByteArrayResource resource = new ByteArrayResource(zipBytes);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipBytes.length)
                    .body(resource);
        } catch (Exception e) {
            log.error("打包选定笔录失败", e);
            throw new RuntimeException("打包选定笔录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取案件统计数据
     */
    @Operation(summary = "获取案件统计数据")
    @GetMapping("/statistics")
    public R<Map<String, Object>> getCaseStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 获取所有案件
            List<CaseEntity> allCases = caseService.list();
            
            // 总案件数
            statistics.put("totalCases", allCases.size());
            
            // 按案件类型统计
            long criminalCases = allCases.stream()
                    .filter(c -> "刑事案件".equals(c.getCaseType()))
                    .count();
            long civilCases = allCases.stream()
                    .filter(c -> "民事案件".equals(c.getCaseType()))
                    .count();
            long administrativeCases = allCases.stream()
                    .filter(c -> "行政案件".equals(c.getCaseType()))
                    .count();
            
            statistics.put("criminalCases", criminalCases);
            statistics.put("civilCases", civilCases);
            statistics.put("administrativeCases", administrativeCases);
            
            // 按案件状态统计
            long investigatingCases = allCases.stream()
                    .filter(c -> "调查中".equals(c.getStatus()))
                    .count();
            long inTrialCases = allCases.stream()
                    .filter(c -> "审理中".equals(c.getStatus()))
                    .count();
            long closedCases = allCases.stream()
                    .filter(c -> "已结案".equals(c.getStatus()) || "已完成".equals(c.getStatus()))
                    .count();
            
            statistics.put("investigatingCases", investigatingCases);
            statistics.put("inTrialCases", inTrialCases);
            statistics.put("closedCases", closedCases);
            
            return R.ok(statistics);
        } catch (Exception e) {
            log.error("获取案件统计数据失败", e);
            return R.fail("获取案件统计数据失败：" + e.getMessage());
        }
    }
}