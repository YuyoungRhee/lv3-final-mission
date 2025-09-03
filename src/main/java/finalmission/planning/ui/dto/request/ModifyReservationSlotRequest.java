package finalmission.planning.ui.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record ModifyReservationSlotRequest(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
