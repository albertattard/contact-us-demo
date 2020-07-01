package demo.boot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table( name = "offices" )
@NoArgsConstructor
@AllArgsConstructor
public class OfficeEntity {

  @Id
  private String name;
  private String address;
  private String country;
  private String phone;
  private String email;
  private String webpage;
}
