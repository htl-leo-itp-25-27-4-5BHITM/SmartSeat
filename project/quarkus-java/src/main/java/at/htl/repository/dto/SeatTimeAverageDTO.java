package at.htl.repository.dto;

import at.htl.model.Seat;

public record SeatTimeAverageDTO(long seat_id, String name, double average) {
}
