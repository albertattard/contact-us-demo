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

  private final EventFoodGateway eventFoodGateway = mock( EventFoodGateway.class );
  private final UuidGeneratorService uuidGeneratorService = mock( UuidGeneratorService.class );
  private final EventRepository eventRepository = mock( EventRepository.class );
  private final EventEntity officeEntity = mock( EventEntity.class );

  @BeforeEach
  public void setUp() {
    reset( eventFoodGateway, uuidGeneratorService, eventRepository, officeEntity );
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions( eventFoodGateway, uuidGeneratorService, eventRepository, officeEntity );
  }

  private Optional<RegistrationConfirmation> register( RegistrationDetails details ) {
    return new EventRegistrationService( eventRepository, uuidGeneratorService, eventFoodGateway )
      .register( details );
  }

  @Test
  @DisplayName( "should return Optional empty when registering to an non existing event" )
  public void shouldReturnOptionalEmptyWhenNotFound() {
    final UUID eventId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );

    when( eventRepository.findById( eq( eventId ) ) ).thenReturn( Optional.empty() );

    final Optional<RegistrationConfirmation> confirmation = register( details );
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

    final Optional<RegistrationConfirmation> confirmation = register( details );
    assertEquals( Optional.empty(), confirmation );

    verify( eventRepository, times( 1 ) ).findById( eventId );
    verify( officeEntity, times( 1 ) ).getDate();
  }

  @Test
  @DisplayName( "should return the registration confirmation and notify the event food gateway when registering to an active event" )
  public void shouldReturnConfirmationWhenActive() {
    final UUID eventId = UUID.randomUUID();
    final UUID attendeeId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );
    final EventAttendeeEntity attendeeEntity = new EventAttendeeEntity( attendeeId, name, foodPreference, officeEntity );
    final AttendeeFoodPreference attendeeFoodPreference = new AttendeeFoodPreference( eventId, attendeeId, foodPreference );

    when( eventRepository.findById( eq( eventId ) ) ).thenReturn( Optional.of( officeEntity ) );
    when( officeEntity.getId() ).thenReturn( eventId );
    when( officeEntity.getDate() ).thenReturn( LocalDate.now().plusDays( 1 ) );
    when( uuidGeneratorService.nextAttendeeId() ).thenReturn( attendeeId );
    doNothing().when( officeEntity ).addAttendee( attendeeEntity );
    when( eventRepository.save( eq( officeEntity ) ) ).thenReturn( officeEntity );
    doNothing().when( eventFoodGateway ).submit( eq( attendeeFoodPreference ) );

    final Optional<RegistrationConfirmation> confirmation = register( details );
    assertTrue( confirmation.isPresent() );

    verify( eventRepository, times( 1 ) ).findById( eventId );
    verify( officeEntity, times( 1 ) ).getId();
    verify( officeEntity, times( 1 ) ).getDate();
    verify( uuidGeneratorService, times( 1 ) ).nextAttendeeId();
    verify( officeEntity, times( 1 ) ).addAttendee( attendeeEntity );
    verify( eventRepository, times( 1 ) ).save( officeEntity );
    verify( eventFoodGateway, times( 1 ) ).submit( attendeeFoodPreference );
  }
}
