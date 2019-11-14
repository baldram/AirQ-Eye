package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.Location;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.Sensor;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorData;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorType;
import pl.itrack.airqeye.store.measurement.domain.model.Measurement;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Country;

/**
 * This test class check only correctness of data filtering for measurements. For full test of
 * recursive data structure conversion please use LuftdatenDataSourceRecursiveMappingTest.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@DisplayName("Handle supported data only while mapping")
class MeasurementMapperTest {

  private static final String SUPPORTED_SENSOR_TYPE = "SDS011";
  private static final String SUPPORTED_VALUE_TYPE = "P1";
  private static final String MEASUREMENT_DATE = "2019-09-29 07:51:04";
  private static final LocalDateTime RESPECTIVE_PARSED_MEASUREMENT_DATE
      = LocalDateTime.of(2019, 9, 29, 7, 51, 4);

  private InstallationMapper installationMapper;

  private MeasurementValueMapper measurementValueMapper;

  private MeasurementMapper measurementMapper;

  @BeforeEach
  void setUp(@Mock MeasurementValueMapper measurementValueMapper,
      @Mock InstallationMapper installationMapper) {
    this.measurementValueMapper = measurementValueMapper;
    this.installationMapper = installationMapper;
    this.measurementMapper = new MeasurementMapperImpl(measurementValueMapper, installationMapper);
  }

  /**
   * For full recursive data conversion test without mocks please use
   * LuftdatenDataSourceRecursiveMappingTest.
   */
  @Test
  @DisplayName("Take data if supported country and sensor type")
  void handleSupportedData() {
    // Given
    List<LuftdatenMeasurement> source = singletonList(
        getLuftdatenDataSample(Country.PL.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE));

    // When
    final List<Measurement> target = measurementMapper.fromDtos(source);

    // Then
    assertThat(target).hasSize(1);
    assertThat(target.get(0).getId()).isNull(); // do not override stuff handled by JPA
    assertThat(target.get(0).getOccurredAtUtc()).isEqualTo(RESPECTIVE_PARSED_MEASUREMENT_DATE);
    verify(measurementValueMapper).fromDtos(anyList());
    verify(installationMapper).fromDto(any());
  }

  private LuftdatenMeasurement getLuftdatenDataSample(String country, String sensorType,
      String valueType) {
    Location location = Location.builder().country(country).build();
    Sensor sensor = Sensor.builder().type(SensorType.builder().name(sensorType).build()).build();

    return LuftdatenMeasurement.builder()
        .id(123L)
        .location(location)
        .timestampUtc(MEASUREMENT_DATE)
        .sensor(sensor)
        .sensorData(singletonList(SensorData.builder().valueType(valueType).value(123d).build()))
        .build();
  }

  @Test
  @DisplayName("Ignore data related to unsupported country")
  void ignoreUnsupportedCountry() {
    // Given
    List<LuftdatenMeasurement> source = Arrays.asList(
        getLuftdatenDataSample(Country.NL.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE),
        getLuftdatenDataSample("XX", SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE),
        getLuftdatenDataSample(Country.PL.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE));

    // When
    final List<Measurement> target = measurementMapper.fromDtos(source);

    // Then
    assertThat(source).hasSize(3);
    assertThat(target).hasSize(2);
  }

  @Test
  @DisplayName("Ignore data related to unsupported sensor type")
  void ignoreUnsupportedSensorType() {
    // Given
    List<LuftdatenMeasurement> source = Arrays.asList(
        getLuftdatenDataSample(Country.CN.toString(), "some-unsupported", SUPPORTED_VALUE_TYPE),
        getLuftdatenDataSample(Country.PL.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE),
        getLuftdatenDataSample(Country.SE.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE));

    // When
    final List<Measurement> target = measurementMapper.fromDtos(source);

    // Then
    assertThat(source).hasSize(3);
    assertThat(target).hasSize(2);
  }

  /**
   * We ignore whole sensor location if no data reads at all is found after filtering out all
   * unsupported value types (like humidity or temperature, when here we look only for dust sensors
   * data).
   */
  @Test
  @DisplayName("Ignore data related to unsupported measurement type")
  void ignoreMeasurementSourceIfNoSupportedDataReads() {
    // Given
    List<LuftdatenMeasurement> source = Arrays.asList(
        getLuftdatenDataSample(Country.CN.toString(), SUPPORTED_SENSOR_TYPE, "humidity"),
        getLuftdatenDataSample(Country.PL.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE),
        getLuftdatenDataSample(Country.SE.toString(), SUPPORTED_SENSOR_TYPE, SUPPORTED_VALUE_TYPE));

    // When
    final List<Measurement> target = measurementMapper.fromDtos(source);

    // Then
    assertThat(source).hasSize(3);
    assertThat(target).hasSize(2);
  }
}
