package dev.serrodcal;

import jakarta.validation.constraints.NotNull;

public record NewLocationRequest(
        @NotNull(message = "name cannot be null") String name,
        @NotNull(message = "longitud cannot be null") Double lon,
        @NotNull(message = "latitude cannot be null")Double lat
) { }
