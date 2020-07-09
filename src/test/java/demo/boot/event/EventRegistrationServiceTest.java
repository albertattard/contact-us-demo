package demo.boot.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName( "Event registration service" )
public class EventRegistrationServiceTest {

  private final UuidGeneratorService uuidGeneratorService = mock( UuidGeneratorService.class );
  private final EventRepository eventRepository = mock( EventRepository.class );
  private final EventEntity officeEntity = mock( EventEntity.class );

  @BeforeEach
  public void setUp() {
    reset( uuidGeneratorService, eventRepository, officeEntity );
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions( uuidGeneratorService, eventRepository, officeEntity );
  }

  private EventRegistrationService withService() {
    return new EventRegistrationService( eventRepository, uuidGeneratorService );
  }

  @Test
  @DisplayName( "should return Optional empty when registering to an non existing event" )
  public void shouldReturnOptionalEmptyWhenNotFound() {
    final UUID eventId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );

    when( eventRepository.findById( eq( eventId ) ) ).thenReturn( Optional.empty() );

    final EventRegistrationService subject = withService();
    final Optional<RegistrationConfirmation> confirmation = subject.register( details );
    assertEquals( Optional.empty(), confirmation );

    verify( eventRepository, times( 1 ) ).findById( eventId );
  }

  @Test
  @DisplayName( "should return Optional empty when registering to an expired event" )
  public void shouldReturnOptionalEmptyWhenExpired() {
    final UUID eventId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );

    when( eventRepository.findById( eq( eventId ) ) ).thenReturn( Optional.of( officeEntity ) );
    when( officeEntity.getDate() ).thenReturn( LocalDate.now().minusDays( 1 ) );

    final EventRegistrationService subject = withService();
    final Optional<RegistrationConfirmation> confirmation = subject.register( details );
    assertEquals( Optional.empty(), confirmation );

    verify( eventRepository, times( 1 ) ).findById( eventId );
    verify( officeEntity, times( 1 ) ).getDate();
  }

  @Test
  @DisplayName( "should return the registration confirmation when registering to an active event" )
  public void shouldReturnConfirmationWhenActive() {
    final UUID eventId = UUID.randomUUID();
    final UUID attendeeId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );
    final EventAttendeeEntity attendeeEntity = new EventAttendeeEntity( attendeeId, name, foodPreference, officeEntity );

    when( eventRepository.findById( eq( eventId ) ) ).thenReturn( Optional.of( officeEntity ) );
    when( officeEntity.getDate() ).thenReturn( LocalDate.now().plusDays( 1 ) );
    when( uuidGeneratorService.nextAttendeeId() ).thenReturn( attendeeId );
    doNothing().when( officeEntity ).addAttendee( attendeeEntity );
    when( eventRepository.save( eq( officeEntity ) ) ).thenReturn( officeEntity );

    final EventRegistrationService subject = withService();
    final Optional<RegistrationConfirmation> confirmation = subject.register( details );
    assertTrue( confirmation.isPresent() );

    verify( eventRepository, times( 1 ) ).findById( eventId );
    verify( officeEntity, times( 1 ) ).getDate();
    verify( uuidGeneratorService, times( 1 ) ).nextAttendeeId();
    verify( officeEntity, times( 1 ) ).addAttendee( attendeeEntity );
    verify( eventRepository, times( 1 ) ).save( officeEntity );
  }
}
