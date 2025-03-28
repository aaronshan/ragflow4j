package com.ragflow4j.server.controller;

import com.ragflow4j.server.entity.Document;
import com.ragflow4j.server.entity.DocumentProcessStatus;
import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.service.KnowledgeService;
import com.ragflow4j.server.service.DocumentService;
import com.ragflow4j.core.loader.DocumentLoader;
import com.ragflow4j.core.loader.DocumentLoaderFactory;
import com.ragflow4j.core.parser.DocumentParser;
import com.ragflow4j.core.parser.DocumentParserFactory;
import com.ragflow4j.core.parser.ParseResult;
import com.ragflow4j.core.splitter.DocumentSplitter;
import com.ragflow4j.core.splitter.SplitterFactory;
import com.ragflow4j.core.vectorstore.VectorStore;
import com.ragflow4j.core.vectorstore.SearchResult;
import com.ragflow4j.core.embedding.DocumentEmbedding;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 知识管理控制器
 * 提供知识库文档的RESTful API接口
 */
@RestController
@RequestMapping("/knowledge")
@Api(tags = "知识管理", description = "知识库文档管理接口")
public class DocumentController {

    private final DocumentService documentService;
    private final KnowledgeService knowledgeService;
    private final VectorStore vectorStore;
    private final Path fileStorageLocation;
    
    @Autowired
    public DocumentController(DocumentService documentService, KnowledgeService knowledgeService, VectorStore vectorStore) {
        this.documentService = documentService;
        this.knowledgeService = knowledgeService;
        this.vectorStore = vectorStore;
        this.fileStorageLocation = Paths.get("./uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    
    @PostMapping("/documents")
    @ApiOperation("创建新文档")
    public ResponseEntity<Document> createDocument(
            @ApiParam("文档信息") @Valid @RequestBody Document document) {
        Document savedDocument = documentService.saveDocument(document);
        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
    }
    
    @GetMapping("/documents/{id}")
    @ApiOperation("根据ID获取文档")
    public ResponseEntity<Document> getDocumentById(
            @ApiParam("文档ID") @PathVariable UUID id) {
        return documentService.findDocumentById(id)
                .map(document -> new ResponseEntity<>(document, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/documents")
    @ApiOperation("获取所有文档（分页）")
    public ResponseEntity<Page<Document>> getAllDocuments(
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Document> documents = documentService.getAllDocuments(pageRequest);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }
    
    @GetMapping("/documents/type/{documentType}")
    @ApiOperation("根据文档类型获取文档")
    public ResponseEntity<List<Document>> getDocumentsByType(
            @ApiParam("文档类型") @PathVariable String documentType) {
        List<Document> documents = documentService.findDocumentsByType(documentType);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }
    
    @GetMapping("/documents/search")
    @ApiOperation("根据标题关键词搜索文档")
    public ResponseEntity<List<Document>> searchDocumentsByTitle(
            @ApiParam("标题关键词") @RequestParam String keyword) {
        List<Document> documents = documentService.searchDocumentsByTitle(keyword);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }
    
    @PutMapping("/documents/{id}")
    @ApiOperation("更新文档")
    public ResponseEntity<Document> updateDocument(
            @ApiParam("文档ID") @PathVariable UUID id,
            @ApiParam("更新的文档信息") @Valid @RequestBody Document document) {
        try {
            Document updatedDocument = documentService.updateDocument(id, document);
            return new ResponseEntity<>(updatedDocument, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/documents/{id}")
    @ApiOperation("删除文档")
    public ResponseEntity<Void> deleteDocument(
            @ApiParam("文档ID") @PathVariable UUID id) {
                documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/documents/vector/{vectorId}")
    @ApiOperation("根据向量ID获取文档")
    public ResponseEntity<Document> getDocumentByVectorId(
            @ApiParam("向量ID") @PathVariable String vectorId) {
        return documentService.findDocumentByVectorId(vectorId)
                .map(document -> new ResponseEntity<>(document, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/documents/upload")
    @ApiOperation("上传文档到知识库")
    public ResponseEntity<Document> uploadDocument(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("知识库ID") @RequestParam("knowledgeBaseId") Long knowledgeBaseId,
            @ApiParam("文档类型") @RequestParam("documentType") String documentType) {
        try {
            // 保存文件到本地存储
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 创建文档对象
            Document document = new Document();
            document.setTitle(file.getOriginalFilename());
            document.setDocumentType(documentType);
            document.setFilePath(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());
            document.setProcessStatus(DocumentProcessStatus.UPLOADED);
            
            // 关联到知识库
            Knowledge knowledge = knowledgeService.findKnowledgeById(knowledgeBaseId)
                    .orElseThrow(() -> new RuntimeException("Knowledge base not found with id: " + knowledgeBaseId));
            document.setKnowledge(knowledge);
            
            // 保存文档
            Document savedDocument = documentService.saveDocument(document);
            
            // 异步处理文档
            processDocumentAsync(savedDocument);
            
            return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/documents/download/{id}")
    @ApiOperation("下载文档")
    public ResponseEntity<Resource> downloadDocument(@ApiParam("文档ID") @PathVariable UUID id) {
        try {
            Optional<Document> documentOpt = documentService.findDocumentById(id);
            if (!documentOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            Document document = documentOpt.get();
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(document.getMimeType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getTitle() + "\"")
                        .body(resource);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/documents/{id}/status")
    @ApiOperation("获取文档处理状态")
    public ResponseEntity<DocumentProcessStatus> getDocumentProcessStatus(
            @ApiParam("文档ID") @PathVariable UUID id) {
        return documentService.findDocumentById(id)
                .map(document -> new ResponseEntity<>(document.getProcessStatus(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 异步处理文档：加载、解析、切分和向量化
     * 
     * @param document 要处理的文档
     * @return 处理完成的Future
     */
    private CompletableFuture<Void> processDocumentAsync(Document document) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 1. 更新状态为解析中
                document.setProcessStatus(DocumentProcessStatus.PARSING);
                documentService.saveDocument(document);
                
                // 2. 加载文档
                Path filePath = Paths.get(document.getFilePath());
                DocumentLoader loader = DocumentLoaderFactory.getLoader(filePath)
                    .orElseThrow(() -> new RuntimeException("No suitable document loader found for file: " + filePath));
                String content = loader.load(filePath);
                
                // 3. 解析文档
                DocumentParser parser = DocumentParserFactory.getParser(document.getMimeType());
                ParseResult parseResult = parser.parse(content);
                
                // 4. 更新文档内容和状态
                document.setTitle(parseResult.getTitle());
                document.setContent(content);
                document.setProcessStatus(DocumentProcessStatus.PARSED);
                documentService.saveDocument(document);
                
                // 5. 切分文档
                document.setProcessStatus(DocumentProcessStatus.SPLITTING);
                documentService.saveDocument(document);
                
                DocumentSplitter splitter = SplitterFactory.getSplitter(document.getDocumentType());
                List<String> chunks = splitter.split(content);
                
                document.setProcessStatus(DocumentProcessStatus.SPLIT);
                documentService.saveDocument(document);
                
                // 6. 向量化处理
                document.setProcessStatus(DocumentProcessStatus.VECTORIZING);
                documentService.saveDocument(document);
                
                try {
                    // 使用DocumentEmbedding服务将文本块转换为向量
                    List<float[]> vectors = new ArrayList<>();
                    List<String> metadatas = new ArrayList<>();
                    
                    for (int i = 0; i < chunks.size(); i++) {
                        String chunk = chunks.get(i);
                        // 构建元数据JSON
                        String metadata = String.format("{\"documentId\":\"%s\",\"chunkIndex\":%d,\"title\":\"%s\"}", 
                                document.getId().toString(), i, document.getTitle());
                        
                        // 将文本转换为向量
                        float[] vector = vectorStore instanceof DocumentEmbedding ? 
                                ((DocumentEmbedding) vectorStore).embed(chunk) : 
                                new float[0]; // 如果vectorStore不是DocumentEmbedding的实例，则使用空向量
                        
                        vectors.add(vector);
                        metadatas.add(metadata);
                    }
                    
                    // 将向量存储到向量数据库
                    CompletableFuture<Boolean> future = vectorStore.addVectors(vectors, metadatas);
                    Boolean result = future.get(); // 等待向量存储完成
                    
                    if (result) {
                        // 更新文档状态为已向量化
                        document.setProcessStatus(DocumentProcessStatus.VECTORIZED);
                        // 设置向量ID（这里假设使用文档ID作为向量ID）
                        document.setVectorId(document.getId().toString());
                        documentService.saveDocument(document);
                    } else {
                        throw new RuntimeException("Failed to store vectors in vector database");
                    }
                } catch (Exception e) {
                    document.setProcessStatus(DocumentProcessStatus.FAILED);
                    document.setFailureReason("Vector processing failed: " + e.getMessage());
                    documentService.saveDocument(document);
                    throw e; // 重新抛出异常以便外层catch捕获
                }
                
            } catch (Exception e) {
                document.setProcessStatus(DocumentProcessStatus.FAILED);
                document.setFailureReason(e.getMessage());
                documentService.saveDocument(document);
            }
        });
    }
}