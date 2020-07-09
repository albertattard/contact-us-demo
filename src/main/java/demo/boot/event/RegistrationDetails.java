package demo.boot.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDetails {

  private UUID eventId;
  private String name;
  private FoodPreference foodPreference;

}
