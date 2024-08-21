package dev.serrodcal;

import jakarta.validation.constraints.NotNull;

public record NewLocationRequest(
        @NotNull(message = "latitude cannot be null") Double lat,
        @NotNull(message = "longitud cannot be null")Double lon
) { }
