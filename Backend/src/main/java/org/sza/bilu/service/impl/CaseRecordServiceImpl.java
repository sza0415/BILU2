package org.sza.bilu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.sza.bilu.entity.CaseEntity;
import org.sza.bilu.entity.CaseRecord;
import org.sza.bilu.mapper.CaseRecordMapper;
import org.sza.bilu.service.CaseRecordService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 笔录记录服务实现类
 */
@Service
@Slf4j
public class CaseRecordServiceImpl extends ServiceImpl<CaseRecordMapper, CaseRecord> implements CaseRecordService {

    // HMAC密钥，实际应用中应该从安全配置中获取
    private static final byte[] HMAC_KEY = "BiluSecretKeyForHMAC".getBytes(StandardCharsets.UTF_8);

    @Override
    public String calculateContentHash(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        
        // 使用HMAC算法计算哈希值
        return new HmacUtils(HmacAlgorithms.HMAC_MD5, HMAC_KEY).hmacHex(content);
    }

    @Override
    public boolean verifyContentHash(String content, String hash) {
        if (content == null || hash == null) {
            return false;
        }
        
        // 计算内容的哈希值并与提供的哈希值比较
        String calculatedHash = calculateContentHash(content);
        return hash.equals(calculatedHash);
    }
    
    @Override
    public byte[] generateWordDocument(CaseRecord record, CaseEntity caseInfo, boolean calculateHash) {
        try {
            // 创建Word文档
            XWPFDocument document = new XWPFDocument();
            
            // 设置文档标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("笔录: " + record.getRecordName());
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            
            // 添加案件信息（如果提供）
            if (caseInfo != null) {
                XWPFParagraph caseParagraph1 = document.createParagraph();
                XWPFRun caseRun1 = caseParagraph1.createRun();
                caseRun1.setText("案件名称: " + caseInfo.getCaseName());
                caseRun1.setFontSize(12);
                
                XWPFParagraph caseParagraph2 = document.createParagraph();
                XWPFRun caseRun2 = caseParagraph2.createRun();
                caseRun2.setText("案件编号: " + caseInfo.getCaseNumber());
                caseRun2.setFontSize(12);
            }
            
            // 添加笔录基本信息
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            XWPFParagraph infoParagraph1 = document.createParagraph();
            XWPFRun infoRun1 = infoParagraph1.createRun();
            infoRun1.setText("询问时间: " + (record.getInquiryTime() != null ? record.getInquiryTime().format(formatter) : ""));
            infoRun1.setFontSize(12);
            
            XWPFParagraph infoParagraph2 = document.createParagraph();
            XWPFRun infoRun2 = infoParagraph2.createRun();
            infoRun2.setText("询问地点: " + record.getInquiryAddress());
            infoRun2.setFontSize(12);
            
            XWPFParagraph infoParagraph3 = document.createParagraph();
            XWPFRun infoRun3 = infoParagraph3.createRun();
            infoRun3.setText("询问人: " + record.getInquirerName());
            infoRun3.setFontSize(12);
            
            XWPFParagraph infoParagraph4 = document.createParagraph();
            XWPFRun infoRun4 = infoParagraph4.createRun();
            infoRun4.setText("记录人: " + record.getAssistantName());
            infoRun4.setFontSize(12);
            
            XWPFParagraph infoParagraph5 = document.createParagraph();
            XWPFRun infoRun5 = infoParagraph5.createRun();
            infoRun5.setText("询问人ID: " + record.getInquiryId());
            infoRun5.setFontSize(12);
            
            // 添加分隔线
            XWPFParagraph separator = document.createParagraph();
            XWPFRun separatorRun = separator.createRun();
            separatorRun.setText(generateRepeatedString("=", 50));
            
            // 添加笔录内容
            XWPFParagraph contentTitle = document.createParagraph();
            XWPFRun contentTitleRun = contentTitle.createRun();
            contentTitleRun.setText("笔录内容:");
            contentTitleRun.setBold(true);
            contentTitleRun.setFontSize(14);
            
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(record.getTextContent() != null ? record.getTextContent() : "");
            contentRun.setFontSize(12);
            
            // 添加文档结束分隔线
            XWPFParagraph endSeparator = document.createParagraph();
            XWPFRun endSeparatorRun = endSeparator.createRun();
            endSeparatorRun.setText(generateRepeatedString("=", 50));
            
            // 保存文档到字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            document.close();
            byte[] documentBytes = baos.toByteArray();
            
            // 如果需要计算哈希值
            if (calculateHash) {
                // 读取文档内容进行哈希加密
                String documentContent = new String(documentBytes, StandardCharsets.UTF_8);
                String contentHash = calculateContentHash(documentContent);
                
                // 将哈希值保存到记录中
                record.setContentHash(contentHash);
                this.updateById(record);
            }
            
            return documentBytes;
        } catch (IOException e) {
            log.error("生成Word文档失败", e);
            throw new RuntimeException("生成Word文档失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] packageRecordsToZip(CaseEntity caseInfo, List<CaseRecord> records) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);
            
            // 为每个笔录生成Word文档并添加到ZIP中
            for (int i = 0; i < records.size(); i++) {
                CaseRecord record = records.get(i);
                
                // 使用通用函数生成文档，包含案件信息，并计算哈希值
                byte[] documentBytes = generateWordDocument(record, caseInfo, true);
                
                // 生成文件名
                String docFilename = String.format("%02d_%s_%s.docx",
                        i + 1,
                        record.getRecordName(),
                        record.getInquiryTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
                
                // 将文档添加到ZIP文件中
                ZipEntry docEntry = new ZipEntry(docFilename);
                zipOut.putNextEntry(docEntry);
                zipOut.write(documentBytes);
                zipOut.closeEntry();
                
                // 检查是否有关联的视频文件
                if (record.getVideoFilePath() != null && !record.getVideoFilePath().isEmpty()) {
                    // 这里应该添加读取视频文件并添加到ZIP的逻辑
                    // 由于Spring Boot项目中可能没有相同的视频文件处理逻辑，我们先省略这部分
                    log.info("记录 {} 存在视频文件路径，但当前实现中未处理视频文件打包", record.getId());
                }
            }
            
            zipOut.close();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("打包记录到ZIP文件失败", e);
            throw new RuntimeException("打包记录到ZIP文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成重复字符串的辅助方法（Java 8兼容替代String.repeat()）
     * @param str 要重复的字符串
     * @param count 重复次数
     * @return 重复后的字符串
     */
    private String generateRepeatedString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}