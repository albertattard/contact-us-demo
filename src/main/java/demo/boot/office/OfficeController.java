package demo.boot.office;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class OfficeController {

  private final ContactUsService service;

  @RequestMapping( "/offices" )
  public List<Office> offices() {
    return service.list();
  }

  @RequestMapping( "/offices/{country}" )
  public List<Office> officesIn( final @PathVariable( "country" ) String country ) {
    return service.findAllInCountry( country );
  }
}
