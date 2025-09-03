package finalmission.planning.application;

import finalmission.planning.domain.PlanDate;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.domain.TimeSlot;
import finalmission.planning.exception.NotFoundException;
import finalmission.planning.infra.repository.ReservationSlotRepository;
import finalmission.planning.ui.dto.request.CreateReservationSlotRequest;
import finalmission.planning.ui.dto.request.ModifyReservationSlotRequest;
import finalmission.planning.ui.dto.response.ReservationSlotResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminReservationSlotService {

    private final ReservationSlotRepository reservationSlotRepository;

    public ReservationSlotResponse registerReservationSlot(CreateReservationSlotRequest request) {
        PlanDate planDate = new PlanDate(request.date());
        TimeSlot timeSlot = new TimeSlot(request.startTime(), request.endTime());
        ReservationSlot reservationSlot = new ReservationSlot(planDate, timeSlot);
        ReservationSlot saved = reservationSlotRepository.save(reservationSlot);
        return ReservationSlotResponse.from(saved);
    }

    public List<ReservationSlotResponse> getAllReservationSlots() {
        List<ReservationSlot> reservationSlots = reservationSlotRepository.findAll();
        return ReservationSlotResponse.from(reservationSlots);
    }

    public ReservationSlotResponse getDetailById(Long reservationSlotId) {
        ReservationSlot reservationSlot = getReservationSlotByIdOrThrow(reservationSlotId);
        return ReservationSlotResponse.from(reservationSlot);
    }

    public ReservationSlotResponse modifyReservationSlot(Long reservationSlotId, ModifyReservationSlotRequest request) {
        ReservationSlot reservationSlot = getReservationSlotByIdOrThrow(reservationSlotId);

        PlanDate newPlanDate = new PlanDate(request.date());
        TimeSlot newTimeSlot = new TimeSlot(request.startTime(), request.endTime());

        reservationSlot.updateInfo(newPlanDate, newTimeSlot);
        return ReservationSlotResponse.from(reservationSlot);
    }

    public void deleteById(Long reservationSlotId) {
        reservationSlotRepository.deleteById(reservationSlotId);
    }

    private ReservationSlot getReservationSlotByIdOrThrow(Long reservationSlotId) {
        return reservationSlotRepository.findById(reservationSlotId)
                .orElseThrow(() -> new NotFoundException("ReservationSlot", reservationSlotId.toString()));
    }
}
