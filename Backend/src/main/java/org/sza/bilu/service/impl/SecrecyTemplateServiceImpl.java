package org.sza.bilu.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.sza.bilu.entity.SecrecyTemplate;
import org.sza.bilu.mapper.SecrecyTemplateMapper;
import org.sza.bilu.service.SecrecyTemplateService;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保密问答模板服务实现类
 */
@Service
@Slf4j
public class SecrecyTemplateServiceImpl extends ServiceImpl<SecrecyTemplateMapper, SecrecyTemplate> implements SecrecyTemplateService {

    @Override
    public Page<SecrecyTemplate> getTemplatesPage(int page, int size) {
        Page<SecrecyTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SecrecyTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SecrecyTemplate::getTemplateType)
                .orderByAsc(SecrecyTemplate::getSortOrder);
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public Page<SecrecyTemplate> getTemplatesByType(String templateType, int page, int size) {
        Page<SecrecyTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SecrecyTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecrecyTemplate::getTemplateType, templateType)
                .orderByAsc(SecrecyTemplate::getSortOrder);
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public List<SecrecyTemplate> getTemplatesByType(String templateType) {
        LambdaQueryWrapper<SecrecyTemplate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecrecyTemplate::getTemplateType, templateType)
                .orderByAsc(SecrecyTemplate::getSortOrder);
        return this.list(queryWrapper);
    }

    @Override
    public List<String> getAllTemplateTypes() {
        // 使用distinct查询所有模板类型
        return this.baseMapper.selectList(null)
                .stream()
                .map(SecrecyTemplate::getTemplateType)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importFromFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new IllegalArgumentException("只支持Excel文件格式");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            List<SecrecyTemplate> templates = new ArrayList<>();

            // 从第二行开始读取数据（第一行是表头）
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String templateType = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                String question = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                int sortOrder = row.getCell(2) != null ? (int) row.getCell(2).getNumericCellValue() : 0;

                if (StringUtils.isNotBlank(templateType) && StringUtils.isNotBlank(question)) {
                    SecrecyTemplate template = new SecrecyTemplate();
                    template.setTemplateType(templateType);
                    template.setQuestion(question);
                    template.setSortOrder(sortOrder);
                    templates.add(template);
                }
            }

            // 批量保存
            if (!templates.isEmpty()) {
                this.saveBatch(templates);
            }
            return templates.size();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importFromCsv(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".csv")) {
            throw new IllegalArgumentException("只支持CSV文件格式");
        }

        List<SecrecyTemplate> templates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // 跳过第一行（表头）
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String templateType = values[0].trim();
                    String question = values[1].trim();
                    int sortOrder = Integer.parseInt(values[2].trim());

                    if (StringUtils.isNotBlank(templateType) && StringUtils.isNotBlank(question)) {
                        SecrecyTemplate template = new SecrecyTemplate();
                        template.setTemplateType(templateType);
                        template.setQuestion(question);
                        template.setSortOrder(sortOrder);
                        templates.add(template);
                    }
                }
            }
        }

        // 批量保存
        if (!templates.isEmpty()) {
            this.saveBatch(templates);
        }
        return templates.size();
    }

    @Override
    public byte[] exportToExcel(String templateType) throws Exception {
        List<SecrecyTemplate> templates;
        if (StringUtils.isNotBlank(templateType)) {
            templates = this.getTemplatesByType(templateType);
        } else {
            LambdaQueryWrapper<SecrecyTemplate> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(SecrecyTemplate::getTemplateType)
                    .orderByAsc(SecrecyTemplate::getSortOrder);
            templates = this.list(queryWrapper);
        }

        // 创建工作簿和工作表
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("保密问答模板");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("模板类型");
            headerRow.createCell(1).setCellValue("问题内容");
            headerRow.createCell(2).setCellValue("排序");

            // 填充数据
            for (int i = 0; i < templates.size(); i++) {
                SecrecyTemplate template = templates.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(template.getTemplateType());
                row.createCell(1).setCellValue(template.getQuestion());
                row.createCell(2).setCellValue(template.getSortOrder());
            }

            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // 输出Excel文件到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}