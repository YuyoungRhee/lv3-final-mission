package finalmission.planning.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToOne
    private ReservationSlot reservationSlot;

    public Reservation(User user, ReservationSlot reservationSlot) {
        this.user = user;
        this.reservationSlot = reservationSlot;
    }

    public boolean isOwnedBy(Long requestUserId) {
        return this.user.getId().equals(requestUserId);
    }

    public LocalDate getDate() {
        return getReservationSlot().getPlanDate().getDate();
    }

    public String getOwnerName() {
        return getUser().getName();
    }

    public TimeSlot getTimeSlot() {
        return reservationSlot.getTimeSlot();
    }

    public void changeReservationSlot(ReservationSlot reservationSlotToChange) {
        this.reservationSlot = reservationSlotToChange;
    }
}
