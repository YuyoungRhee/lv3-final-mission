package finalmission.planning.application;

import finalmission.planning.domain.Reservation;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.exception.NotFoundException;
import finalmission.planning.infra.repository.ReservationRepository;
import finalmission.planning.infra.repository.ReservationSlotRepository;
import finalmission.planning.ui.dto.request.ModifyReservationRequest;
import finalmission.planning.ui.dto.response.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSlotRepository reservationSlotRepository;

    public List<ReservationResponse> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return ReservationResponse.from(reservations);
    }

    public ReservationResponse getDetailById(Long reservationId) {
        Reservation reservation = getReservationByIdOrThrow(reservationId);
        return ReservationResponse.from(reservation);
    }

    public void deleteById(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    public ReservationResponse modifyById(Long reservationId, ModifyReservationRequest request) {
        Reservation reservation = getReservationByIdOrThrow(reservationId);

        ReservationSlot reservationSlotToChange = getReservationSlotByIdOrThrow(request.reservationSlotId());
        reservation.changeReservationSlot(reservationSlotToChange);
        return ReservationResponse.from(reservation);
    }

    private Reservation getReservationByIdOrThrow(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation", reservationId.toString()));
    }

    private ReservationSlot getReservationSlotByIdOrThrow(Long reservationSlotId) {
        return reservationSlotRepository.findById(reservationSlotId)
                .orElseThrow(() -> new NotFoundException("ReservationSlot", reservationSlotId.toString()));
    }
}
