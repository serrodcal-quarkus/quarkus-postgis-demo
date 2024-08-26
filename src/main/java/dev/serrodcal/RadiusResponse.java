package dev.serrodcal;

import java.util.List;

public record RadiusResponse(
        List<LocationResponse> locations
) { }
