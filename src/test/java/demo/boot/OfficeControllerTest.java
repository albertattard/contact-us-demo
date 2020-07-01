package demo.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName( "Office controller" )
@WebMvcTest( OfficeController.class )
public class OfficeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ContactUsService service;

  @Test
  @DisplayName( "should return the list of offices returned by the service" )
  public void shouldReturnTheOffices() throws Exception {
    final Office cologne =
      new Office( "ThoughtWorks Cologne",
        "Lichtstr. 43i, 50825 Cologne, Germany",
        "+49 221 64 30 70 63",
        "contact-de@thoughtworks.com" );
    when( service.list() ).thenReturn( List.of( cologne ) );

    mockMvc.perform( get( "/offices" ) )
      .andExpect( status().isOk() )
      .andExpect( jsonPath( "$" ).isArray() )
      .andExpect( jsonPath( "$", hasSize( 1 ) ) )
      .andExpect( jsonPath( "$.[0].name", is( cologne.getName() ) ) )
      .andExpect( jsonPath( "$.[0].address", is( cologne.getAddress() ) ) )
      .andExpect( jsonPath( "$.[0].phone", is( cologne.getPhone() ) ) )
      .andExpect( jsonPath( "$.[0].email", is( cologne.getEmail() ) ) )
    ;

    verify( service, times( 1 ) ).list();
  }
}
