package finalmission.planning.ui.dto.response;

import finalmission.planning.domain.ReservationSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservationSlotResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
    public static ReservationSlotResponse from(ReservationSlot reservationSlot) {
        return new ReservationSlotResponse(
                reservationSlot.getId(),
                reservationSlot.getPlanDate().getDate(),
                reservationSlot.getTimeSlot().getStartTime(),
                reservationSlot.getTimeSlot().getEndTime());
    }

    public static List<ReservationSlotResponse> from(List<ReservationSlot> reservationSlots) {
        return reservationSlots.stream()
                .map(ReservationSlotResponse::from)
                .toList();
    }
}
