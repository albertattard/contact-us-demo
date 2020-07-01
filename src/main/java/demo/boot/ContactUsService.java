package demo.boot;

import java.util.List;
import java.util.Optional;

public interface ContactUsService {

  List<Office> list();

  Optional<Office> findOneByName( final String name );

  List<Office> findAllInCountry( final String country );

  Optional<Office> update( final Office office );

  Optional<Office> delete( final String name );
}
