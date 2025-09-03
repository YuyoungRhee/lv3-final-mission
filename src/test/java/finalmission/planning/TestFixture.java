package finalmission.planning;

import finalmission.planning.domain.PlanDate;
import finalmission.planning.domain.TimeSlot;
import finalmission.planning.domain.User;
import finalmission.planning.domain.UserRole;
import java.time.LocalDate;
import java.time.LocalTime;

public class TestFixture {

    public static final PlanDate DEFAULT_PLAN_DATE = new PlanDate(LocalDate.now().plusDays(1));
    public static final TimeSlot DEFAULT_TIME_SLOT_1 = new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0));
    public static final TimeSlot DEFAULT_TIME_SLOT_2 = new TimeSlot(LocalTime.of(12, 0), LocalTime.of(14, 0));
    public static final TimeSlot DEFAULT_TIME_SLOT_3 = new TimeSlot(LocalTime.of(14, 0), LocalTime.of(16, 0));

    public static User createNormalUser() {
        return new User("멍구", "test@email.com", "password", UserRole.NORMAL);
    }

    public static User createNormalUserByName(String name) {
        return new User(name, name + "test@email.com", "password", UserRole.NORMAL);
    }

    public static User createAdminUser() {
        return new User("멍구", "test@email.com", "password", UserRole.ADMIN);
    }

}
