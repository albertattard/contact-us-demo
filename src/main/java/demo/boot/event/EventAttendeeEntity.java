package demo.boot.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@Table( name = "events_attendees" )
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendeeEntity {

  @Id
  private UUID id;

  private String name;

  @Enumerated( EnumType.STRING )
  private FoodPreference foodPreference;

  @ManyToOne
  @JoinColumn( name = "event", nullable = false )
  private EventEntity event;
}
