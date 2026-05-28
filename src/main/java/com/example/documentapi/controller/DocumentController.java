package com.example.documentapi.controller;

import com.example.documentapi.dto.DocumentRequestDto;
import com.example.documentapi.dto.DocumentResponseDto;
import com.example.documentapi.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Dokument hochladen
    @PostMapping("/create")
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("data") DocumentRequestDto requestDto
    ) throws IOException {

        DocumentResponseDto response = documentService.uploadDocument(file, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Alle Dokumente anzeigen
    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    // Ein Dokument anhand der ID anzeigen
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    // Dokument herunterladen
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {

        Resource resource = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Dokument löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}