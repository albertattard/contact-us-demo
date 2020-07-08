package demo.boot.office;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Office {

  private String name;
  private String address;
  private String phone;
  private String email;
}
