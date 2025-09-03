package finalmission.planning.ui;

import finalmission.planning.application.AdminReservationService;
import finalmission.planning.ui.dto.request.ModifyReservationRequest;
import finalmission.planning.ui.dto.response.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class ReservationAdminController {

    private final AdminReservationService adminReservationService;

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return adminReservationService.getAllReservations();
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservationById(@PathVariable Long reservationId) {
        return adminReservationService.getDetailById(reservationId);
    }

    @PutMapping("/{reservationId}")
    public ReservationResponse modifyReservation(@PathVariable Long reservationId,
                                                 @RequestBody ModifyReservationRequest request) {
        return adminReservationService.modifyById(reservationId, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reservationId}")
    public void deleteReservation(@PathVariable Long reservationId) {
        adminReservationService.deleteById(reservationId);
    }
}
