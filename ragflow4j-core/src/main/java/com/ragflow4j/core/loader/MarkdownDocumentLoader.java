package com.ragflow4j.core.loader;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class MarkdownDocumentLoader extends AbstractDocumentLoader {
    private static final String[] SUPPORTED_EXTENSIONS = {".md", ".markdown"};
    private final Parser parser;
    private final TextContentRenderer renderer;

    public MarkdownDocumentLoader() {
        this.parser = Parser.builder().build();
        this.renderer = TextContentRenderer.builder()
                .stripNewlines(false)
                .build();
    }

    @Override
    public String load(Path path) throws IOException {
        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Node document = parser.parse(content);
        
        StringBuilder processedContent = new StringBuilder();
        
        // 提取元数据（如果有YAML front matter）
        String metadata = extractMetadata(content);
        if (metadata != null) {
            processedContent.append(metadata).append("\n---\n");
        }
        
        // 提取文档结构
        processedContent.append("Document Structure:\n");
        extractStructure(document, processedContent, 0);
        
        // 添加原始内容
        processedContent.append("\nDocument Content:\n");
        processedContent.append(renderer.render(document));
        
        return processedContent.toString();
    }

    private String extractMetadata(String content) {
        if (content.startsWith("---\n")) {
            int endIndex = content.indexOf("\n---\n", 4);
            if (endIndex != -1) {
                return content.substring(4, endIndex);
            }
        }
        return null;
    }

    private void extractStructure(Node node, StringBuilder builder, int depth) {
        String prefix = createIndent(depth);
        
        if (node instanceof Heading) {
            Heading heading = (Heading) node;
            builder.append(prefix)
                  .append(createHeadingMarker(heading.getLevel()))
                  .append(" ")
                  .append(renderContent(heading))
                  .append("\n");
        } else if (node instanceof ListBlock) {
            builder.append(prefix)
                  .append(node instanceof BulletList ? "* List\n" : "1. Ordered List\n");
        } else if (node instanceof FencedCodeBlock) {
            FencedCodeBlock codeBlock = (FencedCodeBlock) node;
            builder.append(prefix)
                  .append("```")
                  .append(codeBlock.getInfo() != null ? codeBlock.getInfo() : "")
                  .append("\n");
        }
        
        Node child = node.getFirstChild();
        while (child != null) {
            extractStructure(child, builder, depth + 1);
            child = child.getNext();
        }
    }

    private String createIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        return indent.toString();
    }

    private String createHeadingMarker(int level) {
        StringBuilder marker = new StringBuilder();
        for (int i = 0; i < level; i++) {
            marker.append("#");
        }
        return marker.toString();
    }

    private String renderContent(Node node) {
        StringBuilder content = new StringBuilder();
        Node child = node.getFirstChild();
        while (child != null) {
            if (child instanceof Text) {
                content.append(((Text) child).getLiteral());
            }
            child = child.getNext();
        }
        return content.toString();
    }

    @Override
    public boolean supports(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return Arrays.stream(SUPPORTED_EXTENSIONS)
                .anyMatch(fileName::endsWith);
    }
} 