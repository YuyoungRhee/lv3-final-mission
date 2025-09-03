package finalmission.planning;

import finalmission.planning.domain.Reservation;
import finalmission.planning.domain.ReservationSlot;
import finalmission.planning.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DBHelper {

    @PersistenceContext
    EntityManager em;

    public Reservation insertReservation(Reservation reservation) {
        em.persist(reservation);
        em.flush();

        return reservation;
    }

    public ReservationSlot insertReservationSlot(ReservationSlot reservationSlot) {
        em.persist(reservationSlot);
        em.flush();

        return reservationSlot;
    }

    public User insertUser(User user) {
        em.persist(user);
        em.flush();

        return user;
    }
}
