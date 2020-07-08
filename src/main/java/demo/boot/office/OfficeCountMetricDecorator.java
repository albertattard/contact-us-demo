package demo.boot.office;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class OfficeCountMetricDecorator implements ContactUsService {

  @Configuration
  public static class Factory {

    @Bean
    @Primary
    public OfficeCountMetricDecorator create( final MeterRegistry registry, final OfficesRepository repository,
      final JpaContactUsService target ) {
      final Counter officeCounter = createAndInitCounter( registry, repository );
      return new OfficeCountMetricDecorator( officeCounter, target );
    }

    private Counter createAndInitCounter( final MeterRegistry registry, final OfficesRepository repository ) {
      final Counter officeCounter = registry.counter( "app.office.count" );
      officeCounter.increment( repository.count() );
      return officeCounter;
    }
  }

  private final Counter officeCounter;
  private final JpaContactUsService target;

  public OfficeCountMetricDecorator( final Counter officeCounter, final JpaContactUsService target ) {
    this.officeCounter = officeCounter;
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
