package com.ragflow4j.core.loader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * Loader implementation for text documents with automatic charset detection
 */
public class TextDocumentLoader extends AbstractDocumentLoader {
    
    private static final String[] SUPPORTED_EXTENSIONS = {".txt", ".text", ".log"};
    
    @Override
    public String load(Path path) throws IOException {
        validateFile(path);
        
        // First read the bytes
        byte[] bytes = Files.readAllBytes(path);
        
        // If the file is empty, return empty string
        if (bytes.length == 0) {
            return "";
        }
        
        String charset = null;
        int offset = 0;
        
        // First check for BOM
        if (bytes.length >= 2) {
            // Check for UTF-16BE BOM
            if (bytes[0] == (byte)0xFE && bytes[1] == (byte)0xFF) {
                charset = StandardCharsets.UTF_16BE.name();
                offset = 2;
            }
            // Check for UTF-16LE BOM
            else if (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFE) {
                charset = StandardCharsets.UTF_16LE.name();
                offset = 2;
            }
            // Check for UTF-8 BOM
            else if (bytes.length >= 3 && 
                     bytes[0] == (byte)0xEF &&
                     bytes[1] == (byte)0xBB && 
                     bytes[2] == (byte)0xBF) {
                charset = StandardCharsets.UTF_8.name();
                offset = 3;
            }
        }
        
        // If no BOM found, try UniversalDetector
        if (charset == null) {
            UniversalDetector detector = new UniversalDetector(null);
            detector.handleData(bytes, 0, bytes.length);
            detector.dataEnd();
            charset = detector.getDetectedCharset();
            detector.reset();
        }
        
        // If still no charset detected, fallback to UTF-8
        if (charset == null) {
            charset = StandardCharsets.UTF_8.name();
        }
        
        // Read file with detected charset, skipping BOM if present
        return new String(bytes, offset, bytes.length - offset, charset);
    }
    
    @Override
    public boolean supports(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
} 