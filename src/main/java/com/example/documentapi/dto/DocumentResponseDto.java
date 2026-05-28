package com.example.documentapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponseDto {

    private Long id;
    private String title;
    private String description;
    private String originalFileName;
    private String contentType;
    private LocalDateTime uploadedAt;
}