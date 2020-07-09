package demo.boot.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeFoodPreference {

  private UUID eventId;
  private UUID attendeeId;
  private FoodPreference foodPreference;

}
