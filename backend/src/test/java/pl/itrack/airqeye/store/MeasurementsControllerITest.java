package pl.itrack.airqeye.store;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.itrack.airqeye.store.MeasurementTestDataBuilder.prebuildInstallation;
import static pl.itrack.airqeye.store.MeasurementTestDataBuilder.prebuildMeasurement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.itrack.airqeye.store.dataclient.luftdaten.LuftdatenClient;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.entity.MeasurementValue;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.repository.InstallationRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MeasurementsControllerITest {

  private static final Long EXISTING_INSTALLATION_ID = 333L;
  private static final Long MISSING_INSTALLATION_ID = 666L;
  private static final String NOT_EXISTING_INSTALLATION_JSON_MESSAGE = "{\"error\":\"Could not find installation %s\"}";
  private static final String URI_MEASUREMENTS = "/measurements";
  private static final String URI_SELECTED_MEASUREMENTS = "/measurements/%s/%d";

  private static final List<String> SUPPORTED_SENSORS =
      asList("SDS011", "SDS021", "PMS1003", "PMS3003", "PMS5003", "PMS6003", "PMS7003", "HPM",
          "SPS30");

  private static final List<MeasurementType> SUPPORTED_MEASUREMENT_TYPES =
      asList(MeasurementType.PM10, MeasurementType.PM25);

  private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

  @Autowired
  private MockMvc mockMvc;

  @Value("classpath:fixtures/sample-data-luftdaten.json")
  private Resource sampleData;

  @MockBean
  private LuftdatenClient luftdatenClient;

  @Autowired
  private InstallationRepository repository;

  @Before
  public void setUp() throws IOException {
    ResponseEntity<List<LuftdatenMeasurement>> responseEntity =
        new ResponseEntity<>(getSampleData(), HttpStatus.OK);
    when(luftdatenClient.retrieveData()).thenReturn(responseEntity);
  }

  private List<LuftdatenMeasurement> getSampleData() throws IOException {
    File resource = sampleData.getFile();
    String json = new String(Files.readAllBytes(resource.toPath()));
    return objectMapper.readValue(json, new TypeReference<List<LuftdatenMeasurement>>() {
    });
  }

  @Test
  public void shouldRetrieveAndParseLuftdatenMeasurements() throws Exception {
    // given, when
    // for additional debug tracking use result.andDo(MockMvcResultHandlers.print());
    final ResultActions result = this.mockMvc.perform(get(URI_MEASUREMENTS));
    final List<Measurement> data = getResponseBody(result.andReturn());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    assertThat(data).isNotEmpty();
    assertThat(SUPPORTED_SENSORS).containsAll(getUsedSensorTypes(data));
    assertThat(SUPPORTED_MEASUREMENT_TYPES).containsAll(getUsedMeasurementTypes(data));
    assertThat(data.size()).isGreaterThanOrEqualTo(1);
    assertThat(data.size()).isEqualTo(countMeasurementsWithSupportedTypes(data));
  }

  private List<Measurement> getResponseBody(MvcResult result) throws IOException {
    String responseBody = result.getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, new TypeReference<List<Measurement>>() {
    });
  }

  private Set<String> getUsedSensorTypes(List<Measurement> data) {
    return data.stream()
        .map(entry -> entry.getInstallation().getSensor().getDescription())
        .collect(Collectors.toSet());
  }

  private Set<MeasurementType> getUsedMeasurementTypes(List<Measurement> data) {
    return data.stream()
        .flatMap(entry -> entry.getMeasurementValues().stream())
        .map(MeasurementValue::getType)
        .collect(Collectors.toSet());
  }

  private long countMeasurementsWithSupportedTypes(List<Measurement> data) {
    return data.stream()
        .map(entry -> SUPPORTED_MEASUREMENT_TYPES
            .containsAll(asValueTypes(entry.getMeasurementValues())))
        .count();
  }

  private List<MeasurementType> asValueTypes(List<MeasurementValue> values) {
    return values.stream().map(MeasurementValue::getType).collect(Collectors.toList());
  }

  @Test
  public void shouldRetrieveProvideSingleInstallationData() throws Exception {
    // given
    createTestDataWithOneInstallation(EXISTING_INSTALLATION_ID);
    final String apiUri = String
        .format(URI_SELECTED_MEASUREMENTS, Supplier.LUFTDATEN, EXISTING_INSTALLATION_ID);

    // when
    final ResultActions result = this.mockMvc.perform(get(apiUri));
    final List<Measurement> data = getResponseBody(result.andReturn());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    assertThat(data.size()).isGreaterThanOrEqualTo(1);
    assertThat(SUPPORTED_SENSORS).containsAll(getUsedSensorTypes(data));
    assertThat(SUPPORTED_MEASUREMENT_TYPES).containsAll(getUsedMeasurementTypes(data));
    assertThat(data.size()).isEqualTo(countMeasurementsWithSupportedTypes(data));
  }

  private void createTestDataWithOneInstallation(Long installationId) {
    repository.deleteAll();
    final Measurement measurement =
        prebuildMeasurement(LocalDateTime.now().minusYears(5))
            .installation(prebuildInstallation(Supplier.LUFTDATEN)
                .supplierInstallationId(installationId)
                .build())
            .build();
    repository.save(measurement.getInstallation());
    repository.flush();
  }

  @Test
  public void shouldThrowExceptionForNonExistingInstallation() throws Exception {
    // given
    createTestDataWithOneInstallation(EXISTING_INSTALLATION_ID);
    final String apiUri = String
        .format(URI_SELECTED_MEASUREMENTS, Supplier.LUFTDATEN, MISSING_INSTALLATION_ID);

    // when
    final ResultActions result = this.mockMvc.perform(get(apiUri));
    final String responseBody = result.andReturn().getResponse().getContentAsString();

    // then
    result.andExpect(status().is4xxClientError());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    assertThat(responseBody)
        .isEqualTo(NOT_EXISTING_INSTALLATION_JSON_MESSAGE, MISSING_INSTALLATION_ID);
  }
}
