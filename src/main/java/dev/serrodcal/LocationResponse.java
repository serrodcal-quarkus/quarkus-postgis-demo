package dev.serrodcal;

import java.time.LocalDateTime;

public record LocationResponse(Long id, String name, Double lon, Double lat, LocalDateTime createdAt, LocalDateTime updatedAt) { }
