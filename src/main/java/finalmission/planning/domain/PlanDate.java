package finalmission.planning.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanDate {

    private LocalDate date;

    public PlanDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanDate planDate)) {
            return false;
        }
        return Objects.equals(date, planDate.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
