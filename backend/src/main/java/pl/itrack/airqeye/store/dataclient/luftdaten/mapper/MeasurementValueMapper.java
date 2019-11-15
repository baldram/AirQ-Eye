package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import static java.util.stream.Collectors.toList;
import static pl.itrack.airqeye.store.dataclient.luftdaten.config.SupportedDataTypesHelper.MEASUREMENT_TYPE_PM10;
import static pl.itrack.airqeye.store.dataclient.luftdaten.config.SupportedDataTypesHelper.MEASUREMENT_TYPE_PM2_5;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorData;
import pl.itrack.airqeye.store.measurement.domain.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.domain.enumeration.MeasurementUnit;
import pl.itrack.airqeye.store.measurement.domain.model.MeasurementValue;
import pl.itrack.airqeye.store.measurement.infrastructure.mapper.DefaultMapperConfig;

@Mapper(config = DefaultMapperConfig.class)
interface MeasurementValueMapper {

  /**
   * The threshold as defined by Lufdaten, see: checkValues(obj, sel) in their feinstaub-api.js
   * source. (https://github.com/opendata-stuttgart/feinstaub-map-v2/blob/master/src/js/feinstaub-api.js)
   */
  double THRESHOLD_PM10 = 1900d;
  double THRESHOLD_PM25 = 900d;

  Predicate<SensorData> WITH_PM10_RESPECTING_THRESHOLD =
      data -> MEASUREMENT_TYPE_PM10.equals(data.getValueType()) && data.getValue() < THRESHOLD_PM10;

  Predicate<SensorData> WITH_PM25_RESPECTING_THRESHOLD =
      data -> MEASUREMENT_TYPE_PM2_5.equals(data.getValueType())
          && data.getValue() < THRESHOLD_PM25;

  /**
   * Convert original Lufdaten measurement value to the AirQ Monitor supported structure.
   *
   * @param sensorData Luftdaten data structure built out of the original JSON
   * @return transformed measurement data
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "measurement", ignore = true)
  @Mapping(target = "type", source = "sensorData.valueType", qualifiedByName = "measurementType")
  @Mapping(target = "unit", expression = "java(getDefaultUnit())")
  MeasurementValue fromDto(SensorData sensorData);

  @Named("measurementType")
  default MeasurementType fromValueTypeString(String valueType) {
    // The type conversion is simple as for this data source we support only PM10 and PM2.5 dust
    // sensors, exactly as they originally filter out the dust data.
    if (MEASUREMENT_TYPE_PM10.equals(valueType)) {
      return MeasurementType.PM10;
    } else if (MEASUREMENT_TYPE_PM2_5.equals(valueType)) {
      return MeasurementType.PM25;
    }
    return null;
  }

  default MeasurementUnit getDefaultUnit() {
    return MeasurementUnit.MICROGRAMS_PER_CUBIC_METER;
  }

  /**
   * Convert original Lufdaten measurement value data set to the AirQ Monitor supported structure.
   * Only dust sensors PM10 and PM2.5 are supported as it is originally filtered out by Luftdated.
   *
   * <p>Please see here: https://github.com/opendata-stuttgart/feinstaub-map-v2/blob/master/src/js/feinstaub-api.js
   * Search for "fetch(URL)" and "num === 1" to find the java script definition for that.
   *
   * @param sensorData Luftdaten data structure built out of the original JSON
   * @return transformed measurement data
   */
  default List<MeasurementValue> fromDtos(List<SensorData> sensorData) {
    if (sensorData == null) {
      return new ArrayList<>();
    }

    return sensorData.stream()
        .filter(WITH_PM10_RESPECTING_THRESHOLD.or(WITH_PM25_RESPECTING_THRESHOLD))
        .map(this::fromDto)
        .collect(toList());
  }
}
