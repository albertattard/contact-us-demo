package demo.boot.office;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName( "Office controller" )
@WebMvcTest( OfficeController.class )
public class OfficeControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ContactUsService service;

  @BeforeEach
  public void setUp(){
    reset(service);
  }

  @Test
  @DisplayName( "should match URL and HTTP method for the request" )
  public void shouldMatchUrlAndMethod() throws Exception {
    mockMvc.perform(get("/offices"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @DisplayName( "should verify the call for business logics" )
  public void shouldCallBusinessLogics() throws Exception {
    mockMvc.perform(get("/offices"))
      .andDo(print())
      .andExpect(status().isOk());

    verify(service, times(1)).list();
    verifyNoMoreInteractions(service);
  }

  @Test
  @DisplayName( "should verify the call for business logics with a parameter" )
  public void shouldCallBusinessLogicsWithParameter() throws Exception {
    String country = "germany";
    mockMvc.perform(get("/offices/" + country))
      .andDo(print())
      .andExpect(status().isOk());

    verify(service, times(1)).findAllInCountry(eq(country));
    verifyNoMoreInteractions(service);
  }

  @Test
  @DisplayName( "should verify the serialization of output" )
  public void shouldSerializeOutput() throws Exception {
    final Office cologne =
      new Office( "ThoughtWorks Cologne",
        "Lichtstr. 43i, 50825 Cologne, Germany",
        "+49 221 64 30 70 63",
        "contact-de@thoughtworks.com" );
    when(service.list()).thenReturn(List.of(cologne));

    MvcResult mvcResult = mockMvc.perform(get("/offices"))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();

    assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
      objectMapper.writeValueAsString(List.of( cologne )));
    verify(service, times(1)).list();
    verifyNoMoreInteractions(service);
  }

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
      .andDo(print())
      .andExpect( status().isOk() )
      .andExpect( jsonPath( "$" ).isArray() )
      .andExpect( jsonPath( "$", hasSize( 1 ) ) )
      .andExpect( jsonPath( "$.[0].name", is( cologne.getName() ) ) )
      .andExpect( jsonPath( "$.[0].address", is( cologne.getAddress() ) ) )
      .andExpect( jsonPath( "$.[0].phone", is( cologne.getPhone() ) ) )
      .andExpect( jsonPath( "$.[0].email", is( cologne.getEmail() ) ) )
    ;

    verify( service, times( 1 ) ).list();
    verifyNoMoreInteractions(service);
  }
}
