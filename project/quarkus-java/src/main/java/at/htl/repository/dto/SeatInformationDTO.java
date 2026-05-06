package at.htl.repository.dto;

import java.time.LocalDateTime;

public record SeatInformationDTO(long id, String name, boolean status, String floor, String wing, LocalDateTime occupiedSince) {
}
