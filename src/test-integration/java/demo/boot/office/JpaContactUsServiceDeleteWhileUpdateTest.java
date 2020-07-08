package demo.boot.office;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName( "JPA contact us service (delete while updating)" )
@SpringBootTest( webEnvironment = WebEnvironment.NONE )
public class JpaContactUsServiceDeleteWhileUpdateTest {

  @Autowired
  private OfficesRepository repository;

  @Autowired
  private ContactUsService service;

  private static final OfficeEntity ENTITY = new OfficeEntity(
    "ThoughtWorks Test Office",
    "Test Address",
    "Test Country",
    "Test Phone",
    "Test Email",
    "Test Webpage"
  );

  @BeforeEach
  public void setUp() {
    repository.save( ENTITY );
  }

  @AfterEach
  public void tearDown() {
    repository.delete( ENTITY );
  }

  @Test
  @DisplayName( "should not save the office after this is deleted" )
  public void shouldHandleConcurrentUpdates() {
    final Office office = new Office( ENTITY.getName(), "b", "c", "d" ) {
      @Override
      public String getAddress() {
        /* Delete the office between the findById() and save() */
        deleteOfficeFromAnotherThread();
        return super.getAddress();
      }
    };

    service.update( office );

    /* The office should not be in the database, as this was deleted and the update should not succeed */
    final Optional<OfficeEntity> entity = repository.findById( ENTITY.getName() );
    assertThat( entity.isEmpty() ).isTrue();
  }

  private void deleteOfficeFromAnotherThread() {
    try {
      final Thread delete = new Thread( () -> repository.delete( ENTITY ), "DELETE" );
      delete.start();
      delete.join();
    } catch ( InterruptedException e ) {
    }
  }
}
