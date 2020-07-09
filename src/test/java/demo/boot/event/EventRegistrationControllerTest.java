package demo.boot.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName( "Registration controller" )
@WebMvcTest( EventRegistrationController.class )
public class EventRegistrationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EventRegistrationService service;

  @Autowired
  private ObjectMapper jsonObjectMapper;

  @Test
  @DisplayName( "should return not found when registering for an event that does not exists" )
  public void shouldReturnNotFound() throws Exception {
    final UUID eventId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationRequest registrationRequest = new RegistrationRequest( name, foodPreference );
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );

    when( service.register( eq( details ) ) ).thenReturn( Optional.empty() );

    mockMvc
      .perform(
        post( "/event/{eventId}/register", eventId )
          .contentType( MediaType.APPLICATION_JSON )
          .characterEncoding( StandardCharsets.UTF_8.displayName() )
          .content( jsonObjectMapper.writeValueAsString( registrationRequest ) )
      )
      .andExpect( status().isNotFound() )
    ;

    verify( service, times( 1 ) ).register( details );
    verifyNoMoreInteractions( service );
  }

  @Test
  @DisplayName( "should return the registration confirmation when registering for an existing event" )
  public void shouldReturnConfirmation() throws Exception {
    final UUID eventId = UUID.randomUUID();
    final UUID confirmationId = UUID.randomUUID();
    final String name = "Albert Attard";
    final FoodPreference foodPreference = FoodPreference.MEAT;
    final RegistrationRequest registrationRequest = new RegistrationRequest( name, foodPreference );
    final RegistrationDetails details = new RegistrationDetails( eventId, name, foodPreference );

    when( service.register( eq( details ) ) ).thenReturn( Optional.of( new RegistrationConfirmation( confirmationId ) ) );

    mockMvc
      .perform(
        post( "/event/{eventId}/register", eventId )
          .contentType( MediaType.APPLICATION_JSON )
          .characterEncoding( StandardCharsets.UTF_8.displayName() )
          .content( jsonObjectMapper.writeValueAsString( registrationRequest ) )
      )
      .andExpect( status().isCreated() )
      .andExpect( header().string( "Location", "/event/registration/" + confirmationId ) )
      .andExpect( jsonPath( "$" ).isMap() )
      .andExpect( jsonPath( "$.id", is( confirmationId.toString() ) ) )
    ;

    verify( service, times( 1 ) ).register( details );
    verifyNoMoreInteractions( service );
  }
}
