package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorData;
import pl.itrack.airqeye.store.measurement.domain.model.MeasurementValue;
import pl.itrack.airqeye.store.measurement.domain.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.domain.enumeration.MeasurementUnit;

public class MeasurementValueMapperTest {

  private static final String SENSOR_TYPE_PM10 = "P1";
  private static final String SENSOR_TYPE_PM25 = "P2";
  private static final double SENSOR_VALUE = 31.2d;

  private MeasurementValueMapper mapper = Mappers.getMapper(MeasurementValueMapper.class);

  @Test
  public void takeDataForTypePm25() {
    // Given
    final SensorData sensorData = SensorData.builder()
        .valueType(SENSOR_TYPE_PM25)
        .value(SENSOR_VALUE).build();

    // When
    final List<MeasurementValue> convertedValues = mapper
        .fromDtos(Collections.singletonList(sensorData));

    // Then
    assertThat(convertedValues).hasSize(1);
    assertThat(convertedValues.get(0).getType()).isEqualTo(MeasurementType.PM25);
    assertThat(convertedValues.get(0).getUnit())
        .isEqualTo(MeasurementUnit.MICROGRAMS_PER_CUBIC_METER);
    assertThat(convertedValues.get(0).getValue()).isEqualTo(SENSOR_VALUE);
  }

  @Test
  public void takeDataForTypePm10() {
    // Given
    final SensorData sensorData = SensorData.builder()
        .valueType(SENSOR_TYPE_PM10)
        .value(SENSOR_VALUE).build();

    // When
    final List<MeasurementValue> convertedValues = mapper
        .fromDtos(Collections.singletonList(sensorData));

    // Then
    assertThat(convertedValues).hasSize(1);
    assertThat(convertedValues.get(0).getType()).isEqualTo(MeasurementType.PM10);
    assertThat(convertedValues.get(0).getUnit())
        .isEqualTo(MeasurementUnit.MICROGRAMS_PER_CUBIC_METER);
    assertThat(convertedValues.get(0).getValue()).isEqualTo(SENSOR_VALUE);
  }

  @Test
  public void ignoreDataForAnyOtherType() {
    // Given
    final SensorData sensorData = SensorData.builder()
        .valueType("temperature")
        .value(SENSOR_VALUE).build();

    // When
    final List<MeasurementValue> convertedValues = mapper
        .fromDtos(Collections.singletonList(sensorData));

    // Then
    assertThat(convertedValues).isNotNull();
    assertThat(convertedValues).isEmpty();
  }

  @Test
  public void doNotOverrideTechnicalStuff() {
    // Given
    final SensorData sensorData = SensorData.builder()
        .valueType(SENSOR_TYPE_PM10)
        .value(SENSOR_VALUE).build();

    // When
    final List<MeasurementValue> convertedValues = mapper
        .fromDtos(Collections.singletonList(sensorData));

    // Then
    // it is being handled by JPA
    assertThat(convertedValues.get(0).getId()).isNull();
    // the back reference handling from the parent object
    assertThat(convertedValues.get(0).getMeasurement()).isNull();
  }
}
