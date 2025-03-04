package com.ragflow4j.core.loader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Loader implementation for PDF documents using Apache PDFBox
 */
public class PdfDocumentLoader extends AbstractDocumentLoader {

    @Override
    public String load(Path path) throws IOException {
        validateFile(path);
        
        try (PDDocument document = PDDocument.load(path.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            // Extract metadata
            Map<String, String> metadata = extractMetadata(document);
            
            // Combine text and metadata
            StringBuilder result = new StringBuilder();
            result.append("--- Document Metadata ---\n");
            metadata.forEach((key, value) -> result.append(key).append(": ").append(value).append("\n"));
            result.append("\n--- Document Content ---\n");
            result.append(text);
            
            return result.toString();
        }
    }

    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".pdf");
    }
    
    /**
     * Extract metadata from PDF document
     *
     * @param document The PDF document
     * @return Map of metadata key-value pairs
     */
    private Map<String, String> extractMetadata(PDDocument document) {
        Map<String, String> metadata = new HashMap<>();
        PDDocumentInformation info = document.getDocumentInformation();
        
        if (info != null) {
            metadata.put("Title", info.getTitle());
            metadata.put("Author", info.getAuthor());
            metadata.put("Subject", info.getSubject());
            metadata.put("Keywords", info.getKeywords());
            metadata.put("Creator", info.getCreator());
            metadata.put("Producer", info.getProducer());
            metadata.put("Creation Date", info.getCreationDate() != null ? 
                info.getCreationDate().getTime().toString() : null);
            metadata.put("Modification Date", info.getModificationDate() != null ? 
                info.getModificationDate().getTime().toString() : null);
        }
        
        // Remove null values
        metadata.values().removeIf(value -> value == null || value.isEmpty());
        
        return metadata;
    }
} 