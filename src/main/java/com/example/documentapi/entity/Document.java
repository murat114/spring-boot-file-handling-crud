package com.example.documentapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name, unter dem die Datei gespeichert wurde
    private String fileName;

    // Originalname der hochgeladenen Datei
    private String originalFileName;

    // Dateityp, z. B. application/pdf
    private String contentType;

    // Pfad im Dateisystem
    private String filePath;

    // Zusätzlicher Titel vom Benutzer
    private String title;

    // Beschreibung vom Benutzer
    private String description;

    // Upload-Zeitpunkt
    private LocalDateTime uploadedAt;
}