package at.htl.repository.dto;

import java.time.LocalDateTime;

public record HistoryDTO(long seat_id, LocalDateTime dateTime, long minSeconds, long maxSeconds) {
}
