package demo.boot.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

  private String name;
  private FoodPreference foodPreference;
}
