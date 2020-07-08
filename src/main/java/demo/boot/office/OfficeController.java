package demo.boot.office;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class OfficeController {

  private final ContactUsService service;

  @GetMapping( "/offices" )
  public List<Office> offices() {
    return service.list();
  }

  //  @GetMapping( "/offices/{country}" )
  //  public List<Office> officesIn( final @PathVariable( "country" ) String country ) {
  //    return service.findAllInCountry( country );
  //  }
}
