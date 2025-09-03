package finalmission.planning.ui;

import finalmission.planning.application.AdminReservationSlotService;
import finalmission.planning.ui.dto.request.CreateReservationSlotRequest;
import finalmission.planning.ui.dto.request.ModifyReservationSlotRequest;
import finalmission.planning.ui.dto.response.ReservationSlotResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reservationSlots")
@RequiredArgsConstructor
public class AdminReservationSlotController {

    private final AdminReservationSlotService adminReservationSlotService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReservationSlotResponse createReservationSlot(@RequestBody CreateReservationSlotRequest request) {
        return adminReservationSlotService.registerReservationSlot(request);
    }

    @GetMapping
    public List<ReservationSlotResponse> getAllReservationSlots() {
        return adminReservationSlotService.getAllReservationSlots();
    }

    @GetMapping("/{reservationSlotId}")
    public ReservationSlotResponse getReservationSlotById(@PathVariable Long reservationSlotId) {
        return adminReservationSlotService.getDetailById(reservationSlotId);
    }

    @PutMapping("/{reservationSlotId}")
    public ReservationSlotResponse modifyReservationSlot(@PathVariable Long reservationSlotId,
                                                         @RequestBody ModifyReservationSlotRequest request) {
        return adminReservationSlotService.modifyReservationSlot(reservationSlotId, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reservationSlotId}")
    public void deleteReservationSlot(@PathVariable Long reservationSlotId) {
        adminReservationSlotService.deleteById(reservationSlotId);
    }
}
