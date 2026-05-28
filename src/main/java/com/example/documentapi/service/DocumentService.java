package com.example.documentapi.service;

import com.example.documentapi.dto.DocumentRequestDto;
import com.example.documentapi.dto.DocumentResponseDto;
import com.example.documentapi.entity.Document;
import com.example.documentapi.exception.DocumentNotFoundException;
import com.example.documentapi.repository.DocumentRepository;
import org.springframework.core.io.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    // Ordner, in dem Dateien gespeichert werden
    private final Path uploadPath = Paths.get("uploads");

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    //MultipartFile file ist die hochgeladene Datei und zusatzdaten durch requestDto wie Titel oder Beschreibung
    public DocumentResponseDto uploadDocument(MultipartFile file, DocumentRequestDto requestDto) throws IOException {

        // Prüfen, ob Datei leer ist
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Die Datei darf nicht leer sein.");
        }

        // Upload-Ordner erstellen, falls er noch nicht existiert
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Eindeutiger Dateiname, damit keine Datei überschrieben wird
        //UUID.randomUUID() erzeugt eine zufällige eindeutige ID für jede Datei damit nichts überschrieben wird
        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path filePath = uploadPath.resolve(storedFileName); //Hier wird der vollständige Pfad der Datei erstellt.

        // Datei ins Dateisystem kopieren
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Metadaten in der Datenbank speichern
        Document document = Document.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .fileName(storedFileName)
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .filePath(filePath.toString()) // Pfad der gespeicherten Datei.
                .uploadedAt(LocalDateTime.now())
                .build(); // erzeugt das fertige Objekt

        Document savedDocument = documentRepository.save(document); // Hier wird das Dokument in der Datenbank gespeichert.

        return mapToResponseDto(savedDocument); // Das gespeicherte Entity wird in ein Response-DTO umgewandelt und zurückgegeben ist sicherer und sauberer API.
    }

    public List<DocumentResponseDto> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public DocumentResponseDto getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        return mapToResponseDto(document);
    }

    public Resource downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        Path path = Paths.get(document.getFilePath()); //Hier wird aus dem String-Pfad ein Path-Objekt erstellt.

        if (!Files.exists(path)) {
            throw new RuntimeException("Datei existiert nicht mehr im Dateisystem."); // Hier wird geprüft:Existiert die Datei wirklich noch im Dateisystem?
        } //Falls die Datei fehlt: wird eine Exception geworfen.

        //Jetzt wird die Datei als Resource zurückgegeben.
        //FileSystemResource ist eine Implementierung von Resource aus Spring Framework.
        //Sie repräsentiert eine Datei aus dem Dateisystem.
        //Spring kann daraus später automatisch: Datei lesen und Download starten
        return new FileSystemResource(path);
    }

    public void deleteDocument(Long id) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        // Hier wird aus dem gespeicherten Dateipfad ein Path-Objekt erstellt.
        // wandelt diesen String in ein Java-Path-Objekt um.
        //Warum macht man das? Weil viele Dateioperationen in Java mit Path arbeiten:
        // path enthält am ende den Speicherort der Datei als Path-Objekt.
        Path path = Paths.get(document.getFilePath());

        // Datei aus dem Dateisystem löschen
        Files.deleteIfExists(path);

        // Metadaten aus der Datenbank löschen
        documentRepository.delete(document);
    }

    private DocumentResponseDto mapToResponseDto(Document document) {
        return DocumentResponseDto.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .originalFileName(document.getOriginalFileName())
                .contentType(document.getContentType())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}

