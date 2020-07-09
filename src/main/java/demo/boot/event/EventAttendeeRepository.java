package demo.boot.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendeeEntity, UUID> {
}
