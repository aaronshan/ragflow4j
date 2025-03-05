package com.ragflow4j.core.loader;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ooxml.POIXMLProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class WordDocumentLoader extends AbstractDocumentLoader {
    private static final String[] SUPPORTED_EXTENSIONS = {".doc", ".docx"};

    @Override
    public String load(Path path) throws IOException {
        String fileName = path.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".docx")) {
            return loadDocx(path);
        } else if (fileName.endsWith(".doc")) {
            return loadDoc(path);
        }
        throw new IOException("Unsupported file format: " + fileName);
    }

    private String loadDocx(Path path) throws IOException {
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(path))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            StringBuilder content = new StringBuilder();

            // 提取元数据
            POIXMLProperties.CoreProperties properties = document.getProperties().getCoreProperties();
            appendMetadata(content, properties);

            // 提取文档内容
            content.append("\n---\nDocument Content:\n");
            content.append(extractor.getText());

            return content.toString();
        }
    }

    private String loadDoc(Path path) throws IOException {
        try (HWPFDocument document = new HWPFDocument(Files.newInputStream(path))) {
            WordExtractor extractor = new WordExtractor(document);
            StringBuilder content = new StringBuilder();

            // 提取元数据
            appendMetadata(content, document.getSummaryInformation());

            // 提取文档内容
            content.append("\n---\nDocument Content:\n");
            content.append(extractor.getText());

            return content.toString();
        }
    }

    private void appendMetadata(StringBuilder content, POIXMLProperties.CoreProperties properties) {
        content.append("Title: ").append(properties.getTitle() != null ? properties.getTitle() : "").append("\n");
        content.append("Author: ").append(properties.getCreator() != null ? properties.getCreator() : "").append("\n");
        content.append("Subject: ").append(properties.getSubject() != null ? properties.getSubject() : "").append("\n");
        content.append("Keywords: ").append(properties.getKeywords() != null ? properties.getKeywords() : "").append("\n");
        content.append("Created: ").append(properties.getCreated() != null ? properties.getCreated() : "").append("\n");
        content.append("Modified: ").append(properties.getModified() != null ? properties.getModified() : "").append("\n");
    }

    private void appendMetadata(StringBuilder content, org.apache.poi.hpsf.SummaryInformation info) {
        content.append("Title: ").append(info.getTitle() != null ? info.getTitle() : "").append("\n");
        content.append("Author: ").append(info.getAuthor() != null ? info.getAuthor() : "").append("\n");
        content.append("Subject: ").append(info.getSubject() != null ? info.getSubject() : "").append("\n");
        content.append("Keywords: ").append(info.getKeywords() != null ? info.getKeywords() : "").append("\n");
        content.append("Created: ").append(info.getCreateDateTime() != null ? info.getCreateDateTime() : "").append("\n");
        content.append("Last Saved: ").append(info.getLastSaveDateTime() != null ? info.getLastSaveDateTime() : "").append("\n");
    }

    @Override
    public boolean supports(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return Arrays.stream(SUPPORTED_EXTENSIONS)
                .anyMatch(fileName::endsWith);
    }
} 