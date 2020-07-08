package demo.boot.office;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName( "Offices repository" )
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE )
public class OfficesRepositoryTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private OfficesRepository repository;

  private static final OfficeEntity COLOGNE = new OfficeEntity(
    "ThoughtWorks Cologne",
    "Lichtstr. 43i, 50825 Cologne, Germany",
    "Germany",
    "+49 221 64 30 70 63",
    "contact-de@thoughtworks.com",
    "https://www.thoughtworks.com/locations/cologne"
  );

  private static final OfficeEntity MANCHESTER = new OfficeEntity(
    "ThoughtWorks 'ThoughtWorks Manchester'",
    "4th Floor Federation House, 2 Federation St., Manchester M4 4BF, UK",
    "UK",
    "+44 (0)161 923 6810",
    null,
    "https://www.thoughtworks.com/locations/manchester"
  );

  @BeforeEach
  public void before() {
    entityManager.createQuery( "DELETE FROM OfficeEntity" ).executeUpdate();
    entityManager.persist( COLOGNE );
    entityManager.persist( MANCHESTER );
  }

  @Test
  @DisplayName( "should return all offices in the table" )
  public void shouldReturnAll() {
    final List<OfficeEntity> offices = repository.findAll();

    assertThat( offices.size() ).isEqualTo( 2 );
    assertThat( offices ).contains( COLOGNE );
    assertThat( offices ).contains( MANCHESTER );
  }

  @Test
  @DisplayName( "should return all offices for the given country (case insensitive) " )
  public void shouldReturnAllInCountry() {
    final List<OfficeEntity> offices = repository.findAllByCountryIgnoreCase( "germany" );

    assertThat( offices.size() ).isEqualTo( 1 );
    assertThat( offices ).contains( COLOGNE );
    assertThat( offices ).doesNotContain( MANCHESTER );
  }
}
