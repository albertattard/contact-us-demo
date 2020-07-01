package demo.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@DisplayName( "Contact Us application" )
@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT )
public class ContactUsApplicationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName( "should return 200 when the health endpoint is accessed" )
  public void shouldReturn200HealthEndpoint() {
    assertThat( restTemplate.getForEntity( "/health", String.class ) )
      .matches( r -> r.getStatusCode() == HttpStatus.OK );
  }

  @Test
  @DisplayName( "should return the offices" )
  public void shouldReturnTheOffices() {
    final Office cologne =
      new Office( "ThoughtWorks Cologne",
        "Lichtstr. 43i, 50825 Cologne, Germany",
        "+49 221 64 30 70 63",
        "contact-de@thoughtworks.com" );

    assertThat( restTemplate.getForObject( "/offices", Office[].class ) )
      .contains( cologne );
  }
}
