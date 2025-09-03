package finalmission.planning.infra.repository;

import finalmission.planning.domain.ReservationSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, Long> {
}
