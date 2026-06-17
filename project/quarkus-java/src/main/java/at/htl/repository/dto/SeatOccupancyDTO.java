package at.htl.repository.dto;

public record SeatOccupancyDTO(
        long seatId,
        String seatName,
        String hour,
        double occupancy
) {}