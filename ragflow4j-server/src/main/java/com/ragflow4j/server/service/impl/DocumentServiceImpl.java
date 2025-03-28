package com.ragflow4j.server.service.impl;

import com.ragflow4j.server.entity.Document;
import com.ragflow4j.server.repository.DocumentRepository;
import com.ragflow4j.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文档管理服务实现类
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    
    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findDocumentById(UUID id) {
        return documentRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Document> findDocumentsByType(String documentType) {
        return documentRepository.findByDocumentType(documentType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Document> searchDocumentsByTitle(String keyword) {
        return documentRepository.findByTitleContaining(keyword);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }
    
    @Override
    public void deleteDocument(UUID id) {
        documentRepository.deleteById(id);
    }
    
    @Override
    public Document updateDocument(UUID id, Document document) {
        Optional<Document> existingDocument = documentRepository.findById(id);
        if (existingDocument.isPresent()) {
            Document updatedDocument = existingDocument.get();
            updatedDocument.setTitle(document.getTitle());
            updatedDocument.setContent(document.getContent());
            updatedDocument.setDocumentType(document.getDocumentType());
            updatedDocument.setSource(document.getSource());
            updatedDocument.setMetadata(document.getMetadata());
            updatedDocument.setVectorId(document.getVectorId());
            return documentRepository.save(updatedDocument);
        } else {
            throw new RuntimeException("Document not found with id: " + id);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Document> findDocumentByVectorId(String vectorId) {
        return Optional.ofNullable(documentRepository.findByVectorId(vectorId));
    }
}