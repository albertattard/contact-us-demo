package demo.boot.office;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static demo.boot.office.OfficeCountMetricDecorator.Factory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName( "Office count metric" )
public class OfficeCountMetricDecoratorTest {

  @Test
  @DisplayName( "should set the counter to the number of offices in the repository" )
  public void shouldInitCounter() {
    final long initialNumberOfOffices = 10L;
    final String counterName = "app.office.count";

    final MeterRegistry registry = mock( MeterRegistry.class );
    final Counter counter = mock( Counter.class );
    final OfficesRepository repository = mock( OfficesRepository.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );

    when( registry.counter( eq( counterName ) ) ).thenReturn( counter );
    when( repository.count() ).thenReturn( initialNumberOfOffices );

    final Factory factory = new Factory();
    final OfficeCountMetricDecorator decorator = factory.create( registry, repository, target );
    assertThat( decorator ).isNotNull();

    verify( registry, times( 1 ) ).counter( counterName );
    verify( repository, times( 1 ) ).count();
    verify( counter, times( 1 ) ).increment( eq( (double) initialNumberOfOffices ) );
    verifyNoMoreInteractions( registry, repository, counter, target );
  }

  @Test
  @DisplayName( "should call the target list() method without changing the counter's value" )
  public void shouldPassListRequestsThrough() {
    final Counter counter = mock( Counter.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );
    final Office office = mock( Office.class );

    final List<Office> expected = List.of( office );
    when( target.list() ).thenReturn( expected );

    final ContactUsService subject = new OfficeCountMetricDecorator( counter, target );
    final List<Office> actual = subject.list();
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).list();
    verifyNoMoreInteractions( counter, target, office );
  }

  @Test
  @DisplayName( "should call the target findOneByName() method without changing the counter's value" )
  public void shouldPassFindOneByNameRequestsThrough() {
    final Counter counter = mock( Counter.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );
    final Office office = mock( Office.class );

    final String name = "office name";
    final Optional<Office> expected = Optional.of( office );
    when( target.findOneByName( same( name ) ) ).thenReturn( expected );

    final ContactUsService subject = new OfficeCountMetricDecorator( counter, target );
    final Optional<Office> actual = subject.findOneByName( name );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).findOneByName( name );
    verifyNoMoreInteractions( counter, target, office );
  }

  @Test
  @DisplayName( "should call the target findAllInCountry() method without changing the counter's value" )
  public void shouldPassFindAllInCountryRequestsThrough() {
    final Counter counter = mock( Counter.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );
    final Office office = mock( Office.class );

    final String country = "Germany";
    final List<Office> expected = List.of( office );
    when( target.findAllInCountry( same( country ) ) ).thenReturn( expected );

    final ContactUsService subject = new OfficeCountMetricDecorator( counter, target );
    final List<Office> actual = subject.findAllInCountry( country );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).findAllInCountry( country );
    verifyNoMoreInteractions( counter, target, office );
  }

  @Test
  @DisplayName( "should call the target update() method without changing the counter's value" )
  public void shouldPassUpdateRequestsThrough() {
    final Counter counter = mock( Counter.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );
    final Office office = mock( Office.class );

    final Optional<Office> expected = Optional.of( office );
    when( target.update( same( office ) ) ).thenReturn( expected );

    final ContactUsService subject = new OfficeCountMetricDecorator( counter, target );
    final Optional<Office> actual = subject.update( office );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).update( office );
    verifyNoMoreInteractions( counter, target, office );
  }

  @Test
  @DisplayName( "should call the target delete() method for an office that does not exists and without changing the counter's value" )
  public void shouldPassDeleteRequestsThroughAndDoesNotAdjustTheCounter() {
    final Counter counter = mock( Counter.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );
    final Office office = mock( Office.class );

    final String name = "Office name";
    final Optional<Office> expected = Optional.empty();
    when( target.delete( same( name ) ) ).thenReturn( expected );

    final ContactUsService subject = new OfficeCountMetricDecorator( counter, target );
    final Optional<Office> actual = subject.delete( name );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).delete( name );
    verifyNoMoreInteractions( counter, target, office );
  }

  @Test
  @DisplayName( "should call the target delete() method for an office that exists and decrement the counter's value" )
  public void shouldPassDeleteRequestsThroughAndAdjustTheCounter() {
    final Counter counter = mock( Counter.class );
    final JpaContactUsService target = mock( JpaContactUsService.class );
    final Office office = mock( Office.class );

    final String name = "Office name";
    final Optional<Office> expected = Optional.of( office );
    when( target.delete( same( name ) ) ).thenReturn( expected );

    final ContactUsService subject = new OfficeCountMetricDecorator( counter, target );
    final Optional<Office> result = subject.delete( name );
    /* The result will be wrapped in a new Optional, thus we cannot use the isSameAs() for the Optional */
    assertThat( result ).isEqualTo( expected );
    assertThat( result.get() ).isSameAs( office );

    verify( target, times( 1 ) ).delete( name );
    verify( counter, times( 1 ) ).increment( -1D );
    verifyNoMoreInteractions( counter, target, office );
  }
}
