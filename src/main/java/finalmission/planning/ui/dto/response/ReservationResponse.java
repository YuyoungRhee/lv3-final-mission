package finalmission.planning.ui.dto.response;

import finalmission.planning.domain.Reservation;
import java.util.List;

public record ReservationResponse (
        Long id,
        String ownerName,
        ReservationSlotResponse reservationSlot
){
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getName(),
                ReservationSlotResponse.from(reservation.getReservationSlot())
        );
    }

    public static List<ReservationResponse> from(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
