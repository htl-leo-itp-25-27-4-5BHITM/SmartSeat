package at.htl.repository.dto;

import java.util.List;

public record SeatRenameResult(Status status, List<SeatInformationDTO> seats) {
    public enum Status {
        SUCCESS,
        INVALID,
        NOT_FOUND,
        CONFLICT
    }
}
