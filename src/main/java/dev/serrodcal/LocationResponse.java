package dev.serrodcal;

import java.time.LocalDateTime;

public record LocationResponse(Long id, Double lat, Double lon, LocalDateTime createdAt, LocalDateTime updatedAt) { }
