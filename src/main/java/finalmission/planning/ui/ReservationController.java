package finalmission.planning.ui;

import finalmission.planning.application.ReservationService;
import finalmission.planning.auth.ui.CurrentUser;
import finalmission.planning.auth.ui.dto.CurrentUserInfo;
import finalmission.planning.ui.dto.request.CreateReservationRequest;
import finalmission.planning.ui.dto.request.ModifyReservationRequest;
import finalmission.planning.ui.dto.response.ReservationResponse;
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
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReservationResponse createReservation(@RequestBody CreateReservationRequest request,
                                                 @CurrentUser CurrentUserInfo currentUserInfo) {
        return reservationService.registerReservation(request, currentUserInfo.id());
    }

    @GetMapping
    public List<ReservationResponse> getReservationsByUser(@CurrentUser CurrentUserInfo currentUserInfo) {
        return reservationService.getReservationsByUser(currentUserInfo.id());
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservationById(@PathVariable Long reservationId,
                                                  @CurrentUser CurrentUserInfo currentUserInfo) {
        return reservationService.getDetailById(reservationId, currentUserInfo);
    }

    @PutMapping("/{reservationId}")
    public ReservationResponse modifyReservation(@PathVariable Long reservationId,
                                                 @RequestBody ModifyReservationRequest request,
                                                 @CurrentUser CurrentUserInfo currentUserInfo) {
        return reservationService.modifyById(reservationId, request, currentUserInfo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reservationId}")
    public void deleteReservation(@PathVariable Long reservationId,
                                  @CurrentUser CurrentUserInfo currentUserInfo) {
        reservationService.deleteById(reservationId, currentUserInfo);
    }
}
