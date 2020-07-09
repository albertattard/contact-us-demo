package demo.boot.event;

import demo.boot.office.OfficeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table( name = "events" )
@AllArgsConstructor
@NoArgsConstructor
public class EventEntity {

  @Id
  private UUID id;
  private LocalDate date;
  private String caption;
  private String description;

  @ManyToOne
  @JoinColumn( name = "office", nullable = false )
  private OfficeEntity office;

  @OneToMany( mappedBy = "event" )
  private List<EventAttendeeEntity> attendees = new ArrayList<>();

  public void addAttendee( final EventAttendeeEntity attendee ) {
    attendees.add( attendee );
  }
}
