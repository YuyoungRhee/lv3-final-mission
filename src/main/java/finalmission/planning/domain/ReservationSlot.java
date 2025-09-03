package finalmission.planning.domain;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReservationSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PlanDate planDate;

    @Embedded
    private TimeSlot timeSlot;

    public ReservationSlot(PlanDate planDate, TimeSlot timeSlot) {
        this.planDate = planDate;
        this.timeSlot = timeSlot;
    }

    public void updateInfo(PlanDate newPlanDate, TimeSlot newTimeSlot) {
        this.planDate = newPlanDate;
        this.timeSlot = newTimeSlot;
    }
}
