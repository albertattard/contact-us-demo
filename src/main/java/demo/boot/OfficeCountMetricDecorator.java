package demo.boot;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class OfficeCountMetricDecorator implements ContactUsService {

  private final Counter officeCounter;
  private final JpaContactUsService target;

  public OfficeCountMetricDecorator( final MeterRegistry registry, final OfficesRepository repository,
    final JpaContactUsService target ) {
    officeCounter = registry.counter( "app.office.count" );
    officeCounter.increment( repository.count() );
    this.target = target;
  }

  @Override
  public List<Office> list() {
    return target.list();
  }

  @Override
  public Optional<Office> findOneByName( final String name ) {
    return target.findOneByName( name );
  }

  @Override
  public List<Office> findAllInCountry( final String country ) {
    return target.findAllInCountry( country );
  }

  @Override
  public Optional<Office> update( final Office office ) {
    return target.update( office );
  }

  @Override
  public Optional<Office> delete( final String name ) {
    return target
      .delete( name )
      .map( decrementOfficeCount() );
  }

  private Function<Office, Office> decrementOfficeCount() {
    return office -> {
      officeCounter.increment( -1 );
      return office;
    };
  }
}
