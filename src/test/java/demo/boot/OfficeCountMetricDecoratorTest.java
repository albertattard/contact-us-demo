package demo.boot;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName( "Office count metric" )
public class OfficeCountMetricDecoratorTest {

  private final long initialNumberOfOffices = 10L;
  private final String counterName = "app.office.count";

  private final MeterRegistry registry = mock( MeterRegistry.class );
  private final Counter counter = mock( Counter.class );
  private final OfficesRepository repository = mock( OfficesRepository.class );
  private final JpaContactUsService target = mock( JpaContactUsService.class );
  private final Office office = mock( Office.class );

  @BeforeEach
  public void setUp() {
    reset( registry, repository, counter, target, office );

    when( registry.counter( eq( counterName ) ) ).thenReturn( counter );
    when( repository.count() ).thenReturn( initialNumberOfOffices );
  }

  @AfterEach
  public void tearDown() {
    verify( registry, times( 1 ) ).counter( counterName );
    verify( repository, times( 1 ) ).count();
    verify( counter, times( 1 ) ).increment( eq( (double) initialNumberOfOffices ) );
    verifyNoMoreInteractions( registry, repository, counter, target, office );
  }

  private ContactUsService withService() {
    return new OfficeCountMetricDecorator( registry, repository, target );
  }

  @Test
  @DisplayName( "should set the counter to the number of offices in the repository" )
  public void shouldInitCounter() {
    withService();
  }

  @Test
  @DisplayName( "should call the target list() method without changing the counter's value" )
  public void shouldPassListRequestsThrough() {
    final List<Office> expected = List.of( office );
    when( target.list() ).thenReturn( expected );

    final List<Office> actual = withService().list();
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).list();
  }

  @Test
  @DisplayName( "should call the target findOneByName() method without changing the counter's value" )
  public void shouldPassFindOneByNameRequestsThrough() {
    final String name = "office name";
    final Optional<Office> expected = Optional.of( office );
    when( target.findOneByName( same( name ) ) ).thenReturn( expected );

    final Optional<Office> actual = withService().findOneByName( name );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).findOneByName( name );
  }

  @Test
  @DisplayName( "should call the target findAllInCountry() method without changing the counter's value" )
  public void shouldPassFindAllInCountryRequestsThrough() {
    final String country = "Germany";
    final List<Office> expected = List.of( office );
    when( target.findAllInCountry( same( country ) ) ).thenReturn( expected );

    final List<Office> actual = withService().findAllInCountry( country );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).findAllInCountry( country );
  }

  @Test
  @DisplayName( "should call the target update() method without changing the counter's value" )
  public void shouldPassUpdateRequestsThrough() {
    final Optional<Office> expected = Optional.of( office );
    when( target.update( same( office ) ) ).thenReturn( expected );

    final Optional<Office> actual = withService().update( office );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).update( office );
  }

  @Test
  @DisplayName( "should call the target delete() method for an office that does not exists and without changing the counter's value" )
  public void shouldPassDeleteRequestsThroughAndDoesNotAdjustTheCounter() {
    final String name = "Office name";
    final Optional<Office> expected = Optional.empty();
    when( target.delete( same( name ) ) ).thenReturn( expected );

    final Optional<Office> actual = withService().delete( name );
    assertThat( actual ).isSameAs( expected );

    verify( target, times( 1 ) ).delete( name );
  }

  @Test
  @DisplayName( "should call the target delete() method for an office that exists and decrement the counter's value" )
  public void shouldPassDeleteRequestsThroughAndAdjustTheCounter() {
    final String name = "Office name";
    final Optional<Office> expected = Optional.of( office );
    when( target.delete( same( name ) ) ).thenReturn( expected );

    final Optional<Office> result = withService().delete( name );
    /* The result will be wrapped in a new Optional, thus we cannot use the isSameAs() for the Optional */
    assertThat( result ).isEqualTo( expected );
    assertThat( result.get() ).isSameAs( office );

    verify( target, times( 1 ) ).delete( name );
    verify( counter, times( 1 ) ).increment( -1D );
  }
}
