package demo.boot.event;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventFoodGateway {

  private final String queueName;
  private final AmqpTemplate template;

  public EventFoodGateway( @Value( "${app.queue.food}" ) final String queueName, final AmqpTemplate template ) {
    this.queueName = queueName;
    this.template = template;
  }

  public void submit( final AttendeeFoodPreference preference ) {
    template.convertAndSend( queueName, preference );
  }
}
