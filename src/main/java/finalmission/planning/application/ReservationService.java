package finalmission.planning.application;

import finalmission.planning.auth.exception.ForbiddenException;
import finalmission.planning.auth.ui.dto.CurrentUserInfo;
import finalmission.planning.domain.Reservation;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.domain.User;
import finalmission.planning.domain.UserRole;
import finalmission.planning.exception.NotFoundException;
import finalmission.planning.infra.repository.ReservationRepository;
import finalmission.planning.infra.repository.ReservationSlotRepository;
import finalmission.planning.infra.repository.UserRepository;
import finalmission.planning.ui.dto.request.CreateReservationRequest;
import finalmission.planning.ui.dto.request.ModifyReservationRequest;
import finalmission.planning.ui.dto.response.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationSlotRepository reservationSlotRepository;
    private final EmailService emailService;

    public ReservationResponse registerReservation(CreateReservationRequest request,  Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId.toString()));

        ReservationSlot reservationSlot = getReservationSlotByIdOrThrow(request.reservationSlotId());

        Reservation saved = reservationRepository.save(new Reservation(user, reservationSlot));
        emailService.sendEmailForReservation(saved);

        return ReservationResponse.from(saved);
    }

    public List<ReservationResponse> getReservationsByUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return ReservationResponse.from(reservations);
    }

    public ReservationResponse getDetailById(Long reservationId, CurrentUserInfo currentUserInfo) {
        Reservation reservation = getReservationByIdOrThrow(reservationId);
        validateAccessPermission(currentUserInfo, reservation);
        return ReservationResponse.from(reservation);
    }

    public void deleteById(Long reservationId, CurrentUserInfo currentUserInfo) {
        Reservation reservation = getReservationByIdOrThrow(reservationId);
        validateAccessPermission(currentUserInfo, reservation);
        reservationRepository.delete(reservation);
    }

    public ReservationResponse modifyById(Long reservationId, ModifyReservationRequest request,
                                          CurrentUserInfo currentUserInfo) {
        Reservation reservation = getReservationByIdOrThrow(reservationId);
        validateAccessPermission(currentUserInfo, reservation);

        ReservationSlot reservationSlotToChange = getReservationSlotByIdOrThrow(request.reservationSlotId());
        reservation.changeReservationSlot(reservationSlotToChange);
        return ReservationResponse.from(reservation);
    }

    private void validateAccessPermission(CurrentUserInfo currentUserInfo, Reservation reservation) {
        if(currentUserInfo.role() == UserRole.ADMIN || reservation.isOwnedBy(currentUserInfo.id())) {
            return;
        }
        log.warn("예약 상세 정보 접근 불가 - reservationId={}, userId={}, userRole={}",
                reservation.getId(), currentUserInfo.id(), currentUserInfo.role());
        throw new ForbiddenException();
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
