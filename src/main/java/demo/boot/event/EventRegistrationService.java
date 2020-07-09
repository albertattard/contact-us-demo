package demo.boot.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@AllArgsConstructor
public class EventRegistrationService {

  private final EventRepository eventRepository;
  private final UuidGeneratorService uuidGeneratorService;

  public Optional<RegistrationConfirmation> register( final RegistrationDetails registration ) {
    return eventRepository
      .findById( registration.getEventId() )
      .filter( isNotExpired() )
      .map( registerAttendee( registration ) )
      .map( mapToConfirmation() );
  }

  private Function<EventAttendeeEntity, RegistrationConfirmation> mapToConfirmation() {
    return attendee -> new RegistrationConfirmation( attendee.getId() );
  }

  private Function<EventEntity, EventAttendeeEntity> registerAttendee( final RegistrationDetails registration ) {
    return event -> {
      final EventAttendeeEntity attendee = new EventAttendeeEntity();
      attendee.setId( uuidGeneratorService.nextAttendeeId() );
      attendee.setEvent( event );
      attendee.setName( registration.getName() );
      attendee.setFoodPreference( registration.getFoodPreference() );
      event.addAttendee( attendee );
      eventRepository.save( event );
      return attendee;
    };
  }

  private Predicate<EventEntity> isNotExpired() {
    return event -> LocalDate.now().isBefore( event.getDate() );
  }
}
