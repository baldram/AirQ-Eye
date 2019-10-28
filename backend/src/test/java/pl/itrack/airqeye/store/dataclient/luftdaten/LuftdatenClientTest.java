package pl.itrack.airqeye.store.dataclient.luftdaten;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.measurement.enumeration.Country;

class LuftdatenClientTest {

  private static final String JSON_TEST_DATA = "src/test/resources/sample-luftdaten-data.json";

  private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

  @Test
  @DisplayName("Test if model is consistent with the source JSON and data will be correctly mapped")
  void mapJsonToObject() throws IOException {
    final ResponseEntity<List<LuftdatenMeasurement>> responseEntity = new ResponseEntity<>(
        getSampleData(), HttpStatus.OK);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getBody()).hasSize(1);
    assertThat(responseEntity.getBody().get(0).getId()).isEqualTo(5017919251L);
    assertThat(responseEntity.getBody().get(0).getTimestampUtc()).isEqualTo("2019-09-29 07:51:12");
    assertThat(responseEntity.getBody().get(0).getLocation().getId()).isEqualTo(1160L);
    assertThat(responseEntity.getBody().get(0).getLocation().getLatitude()).isEqualTo(51.154d);
    assertThat(responseEntity.getBody().get(0).getLocation().getLongitude()).isEqualTo(16.94d);
    assertThat(responseEntity.getBody().get(0).getLocation().getAltitude()).isEqualTo(112.7d);
    assertThat(responseEntity.getBody().get(0).getLocation().getCountry())
        .isEqualTo(Country.PL.toString());
    assertThat(responseEntity.getBody().get(0).getSensor().getId()).isEqualTo(2303L);
    assertThat(responseEntity.getBody().get(0).getSensor().getType().getId()).isEqualTo(14L);
    assertThat(responseEntity.getBody().get(0).getSensor().getType().getManufacturer())
        .isEqualTo("Nova Fitness");
    assertThat(responseEntity.getBody().get(0).getSensor().getType().getName()).isEqualTo("SDS011");
    assertThat(responseEntity.getBody().get(0).getSensorData()).hasSize(2);
    assertThat(responseEntity.getBody().get(0).getSensorData().get(0).getId())
        .isEqualTo(10652025843L);
    assertThat(responseEntity.getBody().get(0).getSensorData().get(0).getValue()).isEqualTo(10.3d);
    assertThat(responseEntity.getBody().get(0).getSensorData().get(0).getValueType())
        .isEqualTo("P1");
    assertThat(responseEntity.getBody().get(0).getSensorData().get(1).getId())
        .isEqualTo(10652025849L);
    assertThat(responseEntity.getBody().get(0).getSensorData().get(1).getValue()).isEqualTo(5.3d);
    assertThat(responseEntity.getBody().get(0).getSensorData().get(1).getValueType())
        .isEqualTo("P2");
  }

  private List<LuftdatenMeasurement> getSampleData() throws IOException {
    return objectMapper.readValue(getSampleJson(), new TypeReference<List<LuftdatenMeasurement>>() {
    });
  }

  private String getSampleJson() throws IOException {
    Path path = Paths.get(JSON_TEST_DATA);
    return String.join("", Files.readAllLines(path));
  }
}
