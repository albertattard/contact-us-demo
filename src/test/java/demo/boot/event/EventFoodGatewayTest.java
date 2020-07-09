package demo.boot.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName( "Event food gateway" )
public class EventFoodGatewayTest {

  @Test
  @DisplayName( "should send the given preference to the queue" )
  public void shouldSendMessage() {
    final String queueName = "some-queue-name";
    final AmqpTemplate template = mock( AmqpTemplate.class );

    final AttendeeFoodPreference preference =
      new AttendeeFoodPreference( UUID.randomUUID(), UUID.randomUUID(), FoodPreference.MEAT );

    doNothing().when( template ).convertAndSend( eq( queueName ), eq( preference ) );

    EventFoodGateway gateway = new EventFoodGateway( queueName, template );
    gateway.submit( preference );

    verify( template, times( 1 ) ).convertAndSend( queueName, preference );
    verifyNoMoreInteractions( template );
  }
}
