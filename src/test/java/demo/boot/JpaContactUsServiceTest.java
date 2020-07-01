package demo.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName( "JPA contact us service" )
public class JpaContactUsServiceTest {

  @Test
  @DisplayName( "should return all offices returned by the repository" )
  public void shouldReturnOffices() {
    final List<OfficeEntity> entities = List.of(
      new OfficeEntity( "a1", "a2", "a3", "a4", "a5", "a6" ),
      new OfficeEntity( "b1", "b2", "b3", "b4", "b5", "b6" )
    );

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findAll() ).thenReturn( entities );

    final ContactUsService service = new JpaContactUsService( repository );
    final List<Office> offices = service.list();

    final List<Office> expected = List.of(
      new Office( "a1", "a2", "a4", "a5" ),
      new Office( "b1", "b2", "b4", "b5" )
    );

    assertEquals( expected, offices );

    verify( repository, times( 1 ) ).findAll();
  }

  @Test
  @DisplayName( "should query for the with a given id and return optional empty when the office is not found" )
  public void shouldReturnOptionEmpty() {
    final String id = "a1";

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findById( eq( id ) ) ).thenReturn( Optional.empty() );

    final ContactUsService service = new JpaContactUsService( repository );
    final Optional<Office> office = service.findOneByName( id );

    assertEquals( Optional.empty(), office );

    verify( repository, times( 1 ) ).findById( id );
  }

  @Test
  @DisplayName( "should query for the with a given id and return the office returned by the repository" )
  public void shouldReturnOffice() {
    final String id = "a1";
    final OfficeEntity entity = new OfficeEntity( id, "a2", "a3", "a4", "a5", "a6" );

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findById( eq( id ) ) ).thenReturn( Optional.of( entity ) );

    final ContactUsService service = new JpaContactUsService( repository );
    final Optional<Office> office = service.findOneByName( id );

    final Optional<Office> expected = Optional.of( new Office( "a1", "a2", "a4", "a5" ) );
    assertEquals( expected, office );

    verify( repository, times( 1 ) ).findById( id );
  }

  @Test
  @DisplayName( "should return all offices in a given country" )
  public void shouldReturnOfficesInACountry() {
    final List<OfficeEntity> entities = List.of(
      new OfficeEntity( "a1", "a2", "a3", "a4", "a5", "a6" ),
      new OfficeEntity( "b1", "b2", "b3", "b4", "b5", "b6" )
    );

    final String country = "Germany";

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findAllByCountryIgnoreCase( eq( country ) ) ).thenReturn( entities );

    final ContactUsService service = new JpaContactUsService( repository );
    final List<Office> offices = service.findAllInCountry( country );

    final List<Office> expected = List.of(
      new Office( "a1", "a2", "a4", "a5" ),
      new Office( "b1", "b2", "b4", "b5" )
    );

    assertEquals( expected, offices );

    verify( repository, times( 1 ) ).findAllByCountryIgnoreCase( country );
  }

  @Test
  @DisplayName( "should return empty optional if the office being saved does not exist" )
  public void shouldSaveNonExistentOffice() {
    final String name = "a1";
    final Office office = new Office( name, "a2", "a3", "a4" );

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findById( eq( name ) ) ).thenReturn( Optional.empty() );

    final ContactUsService service = new JpaContactUsService( repository );
    final Optional<Office> saved = service.update( office );

    assertEquals( Optional.empty(), saved );

    verify( repository, times( 1 ) ).findById( name );
    verifyNoMoreInteractions( repository );
  }

  @Test
  @DisplayName( "should update the office and return the updated version" )
  public void shouldSaveOffice() {
    final String name = "a1";
    final Office office = new Office( name, "a2", "a4", "a5" );
    final OfficeEntity existingEntity = new OfficeEntity( name, "o2", "o3", "o4", "o5", "o6" );
    final OfficeEntity updatedEntity = new OfficeEntity( name, "a2", "o3", "a4", "a5", "o6" );

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findById( eq( name ) ) ).thenReturn( Optional.of( existingEntity ) );
    when( repository.save( eq( updatedEntity ) ) ).thenReturn( updatedEntity );

    final ContactUsService service = new JpaContactUsService( repository );
    final Optional<Office> saved = service.update( office );

    assertEquals( Optional.of( office ), saved );

    verify( repository, times( 1 ) ).findById( name );
    verify( repository, times( 1 ) ).save( updatedEntity );
  }

  @Test
  @DisplayName( "should return empty optional if the office being deleted does not exist" )
  public void shouldDeleteNonExistentOffice() {
    final String name = "a1";

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findById( eq( name ) ) ).thenReturn( Optional.empty() );

    final ContactUsService service = new JpaContactUsService( repository );
    final Optional<Office> deleted = service.delete( name );

    assertEquals( Optional.empty(), deleted );

    verify( repository, times( 1 ) ).findById( name );
    verifyNoMoreInteractions( repository );
  }

  @Test
  @DisplayName( "should delete the office and return the deleted office" )
  public void shouldDeleteOffice() {
    final String name = "a1";
    final Office office = new Office( name, "a2", "a4", "a5" );
    final OfficeEntity entity = new OfficeEntity( name, "a2", "a3", "a4", "a5", "a6" );

    final OfficesRepository repository = mock( OfficesRepository.class );
    when( repository.findById( eq( name ) ) ).thenReturn( Optional.of( entity ) );
    doNothing().when( repository ).delete( eq( entity ) );

    final ContactUsService service = new JpaContactUsService( repository );
    final Optional<Office> deleted = service.delete( name );

    assertEquals( Optional.of( office ), deleted );

    verify( repository, times( 1 ) ).findById( name );
    verify( repository, times( 1 ) ).delete( entity );
  }
}
