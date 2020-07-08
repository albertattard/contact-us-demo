package demo.boot.event;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class EventRegistrationController {

  private final EventRegistrationService service;

  @PostMapping( "/event/{eventId}/register" )
  public ResponseEntity<RegistrationConfirmation> register(
    @PathVariable( "eventId" ) final UUID eventId,
    @RequestBody final RegistrationRequest request
  ) {

    final RegistrationDetails details =
      new RegistrationDetails( eventId, request.getName(), request.getFoodPreference() );

    return service
      .register( details )
      .map(
        confirmation -> ResponseEntity
          .created( URI.create( "/event/registration/" + confirmation.getId() ) )
          .body( confirmation )
      )
      .orElse( ResponseEntity.notFound().build() )
      ;
  }
}
