package at.htl.repository.dto;

import java.time.Instant;

public record SeatInformationDTO(
        long id,
        String name,
        boolean status,
        String state,
        String floor,
        String wing,
        Double mapX,
        Double mapY,
        Instant occupiedSince
) {
}
