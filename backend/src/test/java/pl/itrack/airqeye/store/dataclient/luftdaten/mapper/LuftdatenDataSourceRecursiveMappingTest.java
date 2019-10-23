package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.Location;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.Sensor;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorData;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorType;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Country;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementUnit;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;

class LuftdatenDataSourceRecursiveMappingTest {

  private static final String MEASUREMENT_DATE = "2019-09-29 07:51:04";
  private static final LocalDateTime RESPECTIVE_PARSED_MEASUREMENT_DATE
      = LocalDateTime.of(2019, 9, 29, 7, 51, 4);
  private static final double ALTITUDE = 124d;
  private static final Country COUNTRY = Country.PL;
  private static final double LATITUDE = 51.094d;
  private static final double LONGITUDE = 17.002d;
  private static final long LOCATION_ID = 149L;
  private static final long SENSOR_ID = 323L;
  private static final String SENSOR_MANUFACTURER = "Nova Fitness";
  private static final String SENSOR_NAME = "SDS011";
  private static final double SENSOR_VALUE_1 = 2.66d;
  private static final double SENSOR_VALUE_2 = 2.38d;
  private static final String SENSOR_TYPE_1 = "P1";
  private static final String SENSOR_TYPE_2 = "P2";

  private MeasurementMapper measurementMapper;

  @BeforeEach
  void setUpDependencyInjection() {
    LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);
    AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
    SensorMapper sensorMapper = Mappers.getMapper(SensorMapper.class);
    MeasurementValueMapper measurementValueMapper = Mappers.getMapper(MeasurementValueMapper.class);
    InstallationMapper installationMapper = new InstallationMapperImpl(locationMapper,
        addressMapper, sensorMapper);

    measurementMapper = new MeasurementMapperImpl(measurementValueMapper, installationMapper);
  }

  @Test
  void convertLuftdatenResponse() {
    // Given
    LuftdatenMeasurement luftdatenFeed = getSampleLuftdatenResponse();

    // When
    final Measurement result = measurementMapper.fromDto(luftdatenFeed);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNull(); // do not override Entity's id
    assertThat(result.getOccurredAtUtc()).isEqualTo(RESPECTIVE_PARSED_MEASUREMENT_DATE);
    assertThat(result.getInstallation().getId()).isNull();  // do not override Entity's id
    assertThat(result.getInstallation().getLocation().getElevation()).isEqualTo(ALTITUDE);
    assertThat(result.getInstallation().getLocation().getLatitude()).isEqualTo(LATITUDE);
    assertThat(result.getInstallation().getLocation().getLongitude()).isEqualTo(LONGITUDE);
    assertThat(result.getInstallation().getAddress().getCountry()).isEqualTo(COUNTRY);
    assertThat(result.getInstallation().getAddress().getAdditionalAddressDetails())
        .isNull(); // not provided by Luftdaten
    assertThat(result.getInstallation().getAddress().getProvince())
        .isNull();  // not provided by Luftdaten
    assertThat(result.getInstallation().getSupplier()).isEqualTo(Supplier.LUFTDATEN);
    assertThat(result.getInstallation().getSupplierInstallationId()).isEqualTo(LOCATION_ID);
    assertThat(result.getInstallation().getSensor().getSupplierSensorId()).isEqualTo(SENSOR_ID);
    assertThat(result.getInstallation().getSensor().getId())
        .isNull(); // do not override Entity's id
    assertThat(result.getInstallation().getSensor().getManufacturer())
        .isEqualTo(SENSOR_MANUFACTURER);
    assertThat(result.getInstallation().getSensor().getDescription()).isEqualTo(SENSOR_NAME);
    assertThat(result.getInstallation().getMeasurements()).isNotEmpty();
    assertThat(result.getInstallation().getMeasurements()).hasSize(1);
    assertThat(result.getInstallation().getMeasurements().get(0).getOccurredAtUtc())
        .isEqualTo(RESPECTIVE_PARSED_MEASUREMENT_DATE);
    assertThat(result.getMeasurementValues()).hasSize(2);
    assertThat(result.getMeasurementValues().get(0).getId())
        .isNull(); // do not override Entity's id
    assertThat(result.getMeasurementValues().get(0).getType()).isEqualTo(MeasurementType.PM10);
    assertThat(result.getMeasurementValues().get(0).getValue()).isEqualTo(SENSOR_VALUE_1);
    assertThat(result.getMeasurementValues().get(0).getUnit())
        .isEqualTo(MeasurementUnit.MICROGRAMS_PER_CUBIC_METER);
    assertThat(result.getMeasurementValues().get(0).getMeasurement()).isEqualTo(result);
    assertThat(result.getMeasurementValues().get(0).getMeasurement().getInstallation()
        .getSupplierInstallationId()).isEqualTo(LOCATION_ID);
    assertThat(result.getMeasurementValues().get(1).getId())
        .isNull(); // do not override Entity's id
    assertThat(result.getMeasurementValues().get(1).getType()).isEqualTo(MeasurementType.PM25);
    assertThat(result.getMeasurementValues().get(1).getValue()).isEqualTo(SENSOR_VALUE_2);
    assertThat(result.getMeasurementValues().get(1).getUnit())
        .isEqualTo(MeasurementUnit.MICROGRAMS_PER_CUBIC_METER);
    assertThat(result.getMeasurementValues().get(1).getMeasurement()).isEqualTo(result);
    assertThat(result.getMeasurementValues().get(1).getMeasurement().getInstallation()
        .getSupplierInstallationId()).isEqualTo(LOCATION_ID);
  }

  private LuftdatenMeasurement getSampleLuftdatenResponse() {
    return getSampleLuftdatenResponse(COUNTRY.toString());
  }

  private LuftdatenMeasurement getSampleLuftdatenResponse(String country) {
    return getLuftdatenMeasurement(country, MEASUREMENT_DATE);
  }

  private LuftdatenMeasurement getLuftdatenMeasurement(String country, String measurementDate) {
    return LuftdatenMeasurement.builder()
        .id(5017918083L)
        .timestampUtc(measurementDate)
        .location(Location.builder()
            .id(LOCATION_ID)
            .latitude(LATITUDE)
            .longitude(LONGITUDE)
            .altitude(ALTITUDE)
            .country(country)
            .build())
        .sensor(Sensor.builder()
            .id(SENSOR_ID)
            .type(SensorType.of(1L, SENSOR_MANUFACTURER, SENSOR_NAME))
            .build())
        .sensorData(asList(
            SensorData.of(10652023377L, SENSOR_VALUE_1, SENSOR_TYPE_1),
            SensorData.of(10652023398L, SENSOR_VALUE_2, SENSOR_TYPE_2)))
        .build();
  }

  @Test
  void handleMultipleEntries() {
    // Given
    final List<LuftdatenMeasurement> luftdatenFeed = asList(
        getSampleLuftdatenResponse(Country.PL.toString()),
        getSampleLuftdatenResponse(Country.DE.toString()));

    // When
    final List<Measurement> result = measurementMapper.fromDtos(luftdatenFeed);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getInstallation().getAddress().getCountry()).isEqualTo(Country.PL);
    assertThat(result.get(1).getInstallation().getAddress().getCountry()).isEqualTo(Country.DE);
  }

  @Test
  void ignoreUnsupportedCountry() {
    // Given
    final List<LuftdatenMeasurement> luftdatenFeed = asList(
        getSampleLuftdatenResponse("XX"),
        getSampleLuftdatenResponse(Country.PL.toString()));

    // When
    final List<Measurement> result = measurementMapper.fromDtos(luftdatenFeed);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getInstallation().getAddress().getCountry()).isEqualTo(Country.PL);
  }
}


/**
 * id: 1871848769
 * ​
 * location: {…}
 * ​​
 * altitude: "483.8"
 * ​​
 * country: "DE"
 * ​​
 * id: 43
 * ​​
 * latitude: "49.0820"
 * ​​
 * longitude: "9.6100"
 * <p>
 * sensor: {…}
 * ​​
 * id: 81
 * ​​
 * sensor_type: Object { id: 1, name: "PPD42NS", manufacturer: "Shinyei" }
 * ​​
 * sensordatavalues: (6) […]
 * ​​
 * 0: Object { id: 3994830107, value: "306397.50", value_type: "durP1" }
 * ​​
 * 1: Object { id: 3994830110, value: "9535.00", value_type: "durP2" }
 * ​​
 * 2: Object { id: 3994830109, value: "528.90", value_type: "P1" }
 * ​​
 * 3: Object { id: 3994830112, value: "17.14", value_type: "P2" }
 * ​​
 * 4: Object { id: 3994830108, value: "1.02", value_type: "ratioP1" }
 * ​​
 * 5: Object { id: 3994830111, value: "0.03", value_type: "ratioP2" }
 * ​​
 * length: 6
 * ​
 * timestamp: "2018-09-07 19:52:44"
 * <p>
 * <p>
 * sensordatavalues: (6) […]
 * ​​
 * 0: Object { id: 4000571598, value: "136326.50", value_type: "durP1" }
 * ​​
 * 1: Object { id: 4000571601, value: "399056.50", value_type: "durP2" }
 * ​​
 * 2: Object { id: 4000571600, value: "236.21", value_type: "P1" }
 * ​​
 * 3: Object { id: 4000571603, value: "688.19", value_type: "P2" }
 * ​​
 * 4: Object { id: 4000571599, value: "0.46", value_type: "ratioP1" }
 * ​​
 * 5: Object { id: 4000571602, value: "1.33", value_type: "ratioP2" }
 * <p>
 * ---
 * <p>
 * sensordatavalues: (3) […]
 * ​​
 * 0: Object { id: 4000567541, value: "11.00", value_type: "P0" }
 * ​​
 * 1: Object { id: 4000567542, value: "16.75", value_type: "P1" }
 * ​​
 * 2: Object { id: 4000567543, value: "14.75", value_type: "P2" }
 * <p>
 * ---
 * <p>
 * sensordatavalues: (2) […]
 * ​​
 * 0: Object { id: 4000559762, value: "9.95", value_type: "P1" }
 * ​​
 * 1: Object { id: 4000559764, value: "4.30", value_type: "P2" }
 * <p>
 * {
 * "occurredAt": "2019-09-30T16:17:44",
 * "installation": {
 * "location": {
 * "latitude": 50.932,
 * "longitude": 4.04,
 * "elevation": 15.8
 * },
 * "address": {
 * "country": "BE",
 * "province": null,
 * "additionalAddressDetails": null
 * },
 * "supplier": "LUFTDATEN",
 * "supplierInstallationId": 17983,
 * "sensor": {
 * "supplierSensorId": 31352,
 * "manufacturer": "Nova Fitness",
 * "description": "SDS011"
 * }
 * },
 * "measurementValues": [
 * {
 * "type": "PM10",
 * "value": 8.64,
 * "unit": "MICROGRAMS_PER_CUBIC_METER"
 * },
 * {
 * "type": "PM25",
 * "value": 1.95,
 * "unit": "MICROGRAMS_PER_CUBIC_METER"
 * }
 * ]
 * },
 * <p>
 * {
 * "occurredAt": "2019-09-30T16:17:39",
 * "installation": {
 * "location": {
 * "latitude": 51.044,
 * "longitude": 13.746,
 * "elevation": 115.3
 * },
 * "address": {
 * "country": "DE",
 * "province": null,
 * "additionalAddressDetails": null
 * },
 * "supplier": "LUFTDATEN",
 * "supplierInstallationId": 13208,
 * "sensor": {
 * "supplierSensorId": 36,
 * "manufacturer": "Nova Fitness",
 * "description": "SDS011"
 * }
 * },
 * "measurementValues": [
 * {
 * "type": null,
 * "value": 40.4,
 * "unit": null
 * }
 * ]
 * },
 * <p>
 * sensordatavalues: (6) […]
 * ​​
 * 0: Object { id: 4000571598, value: "136326.50", value_type: "durP1" }
 * ​​
 * 1: Object { id: 4000571601, value: "399056.50", value_type: "durP2" }
 * ​​
 * 2: Object { id: 4000571600, value: "236.21", value_type: "P1" }
 * ​​
 * 3: Object { id: 4000571603, value: "688.19", value_type: "P2" }
 * ​​
 * 4: Object { id: 4000571599, value: "0.46", value_type: "ratioP1" }
 * ​​
 * 5: Object { id: 4000571602, value: "1.33", value_type: "ratioP2" }
 * <p>
 * ---
 * <p>
 * sensordatavalues: (3) […]
 * ​​
 * 0: Object { id: 4000567541, value: "11.00", value_type: "P0" }
 * ​​
 * 1: Object { id: 4000567542, value: "16.75", value_type: "P1" }
 * ​​
 * 2: Object { id: 4000567543, value: "14.75", value_type: "P2" }
 * <p>
 * ---
 * <p>
 * sensordatavalues: (2) […]
 * ​​
 * 0: Object { id: 4000559762, value: "9.95", value_type: "P1" }
 * ​​
 * 1: Object { id: 4000559764, value: "4.30", value_type: "P2" }
 * <p>
 * {
 * "occurredAt": "2019-09-30T16:17:44",
 * "installation": {
 * "location": {
 * "latitude": 50.932,
 * "longitude": 4.04,
 * "elevation": 15.8
 * },
 * "address": {
 * "country": "BE",
 * "province": null,
 * "additionalAddressDetails": null
 * },
 * "supplier": "LUFTDATEN",
 * "supplierInstallationId": 17983,
 * "sensor": {
 * "supplierSensorId": 31352,
 * "manufacturer": "Nova Fitness",
 * "description": "SDS011"
 * }
 * },
 * "measurementValues": [
 * {
 * "type": "PM10",
 * "value": 8.64,
 * "unit": "MICROGRAMS_PER_CUBIC_METER"
 * },
 * {
 * "type": "PM25",
 * "value": 1.95,
 * "unit": "MICROGRAMS_PER_CUBIC_METER"
 * }
 * ]
 * },
 * <p>
 * {
 * "occurredAt": "2019-09-30T16:17:39",
 * "installation": {
 * "location": {
 * "latitude": 51.044,
 * "longitude": 13.746,
 * "elevation": 115.3
 * },
 * "address": {
 * "country": "DE",
 * "province": null,
 * "additionalAddressDetails": null
 * },
 * "supplier": "LUFTDATEN",
 * "supplierInstallationId": 13208,
 * "sensor": {
 * "supplierSensorId": 36,
 * "manufacturer": "Nova Fitness",
 * "description": "SDS011"
 * }
 * },
 * "measurementValues": [
 * {
 * "type": null,
 * "value": 40.4,
 * "unit": null
 * }
 * ]
 * },
 * <p>
 * sensordatavalues: (6) […]
 * ​​
 * 0: Object { id: 4000571598, value: "136326.50", value_type: "durP1" }
 * ​​
 * 1: Object { id: 4000571601, value: "399056.50", value_type: "durP2" }
 * ​​
 * 2: Object { id: 4000571600, value: "236.21", value_type: "P1" }
 * ​​
 * 3: Object { id: 4000571603, value: "688.19", value_type: "P2" }
 * ​​
 * 4: Object { id: 4000571599, value: "0.46", value_type: "ratioP1" }
 * ​​
 * 5: Object { id: 4000571602, value: "1.33", value_type: "ratioP2" }
 * <p>
 * ---
 * <p>
 * sensordatavalues: (3) […]
 * ​​
 * 0: Object { id: 4000567541, value: "11.00", value_type: "P0" }
 * ​​
 * 1: Object { id: 4000567542, value: "16.75", value_type: "P1" }
 * ​​
 * 2: Object { id: 4000567543, value: "14.75", value_type: "P2" }
 * <p>
 * ---
 * <p>
 * sensordatavalues: (2) […]
 * ​​
 * 0: Object { id: 4000559762, value: "9.95", value_type: "P1" }
 * ​​
 * 1: Object { id: 4000559764, value: "4.30", value_type: "P2" }
 * <p>
 * {
 * "occurredAt": "2019-09-30T16:17:44",
 * "installation": {
 * "location": {
 * "latitude": 50.932,
 * "longitude": 4.04,
 * "elevation": 15.8
 * },
 * "address": {
 * "country": "BE",
 * "province": null,
 * "additionalAddressDetails": null
 * },
 * "supplier": "LUFTDATEN",
 * "supplierInstallationId": 17983,
 * "sensor": {
 * "supplierSensorId": 31352,
 * "manufacturer": "Nova Fitness",
 * "description": "SDS011"
 * }
 * },
 * "measurementValues": [
 * {
 * "type": "PM10",
 * "value": 8.64,
 * "unit": "MICROGRAMS_PER_CUBIC_METER"
 * },
 * {
 * "type": "PM25",
 * "value": 1.95,
 * "unit": "MICROGRAMS_PER_CUBIC_METER"
 * }
 * ]
 * },
 * <p>
 * {
 * "occurredAt": "2019-09-30T16:17:39",
 * "installation": {
 * "location": {
 * "latitude": 51.044,
 * "longitude": 13.746,
 * "elevation": 115.3
 * },
 * "address": {
 * "country": "DE",
 * "province": null,
 * "additionalAddressDetails": null
 * },
 * "supplier": "LUFTDATEN",
 * "supplierInstallationId": 13208,
 * "sensor": {
 * "supplierSensorId": 36,
 * "manufacturer": "Nova Fitness",
 * "description": "SDS011"
 * }
 * },
 * "measurementValues": [
 * {
 * "type": null,
 * "value": 40.4,
 * "unit": null
 * }
 * ]
 * },
 */

/**
 sensordatavalues: (6) […]
 ​​
 0: Object { id: 4000571598, value: "136326.50", value_type: "durP1" }
 ​​
 1: Object { id: 4000571601, value: "399056.50", value_type: "durP2" }
 ​​
 2: Object { id: 4000571600, value: "236.21", value_type: "P1" }
 ​​
 3: Object { id: 4000571603, value: "688.19", value_type: "P2" }
 ​​
 4: Object { id: 4000571599, value: "0.46", value_type: "ratioP1" }
 ​​
 5: Object { id: 4000571602, value: "1.33", value_type: "ratioP2" }

 ---

 sensordatavalues: (3) […]
 ​​
 0: Object { id: 4000567541, value: "11.00", value_type: "P0" }
 ​​
 1: Object { id: 4000567542, value: "16.75", value_type: "P1" }
 ​​
 2: Object { id: 4000567543, value: "14.75", value_type: "P2" }

 ---

 sensordatavalues: (2) […]
 ​​
 0: Object { id: 4000559762, value: "9.95", value_type: "P1" }
 ​​
 1: Object { id: 4000559764, value: "4.30", value_type: "P2" }
 */


/**
 {
 "occurredAt": "2019-09-30T16:17:44",
 "installation": {
 "location": {
 "latitude": 50.932,
 "longitude": 4.04,
 "elevation": 15.8
 },
 "address": {
 "country": "BE",
 "province": null,
 "additionalAddressDetails": null
 },
 "supplier": "LUFTDATEN",
 "supplierInstallationId": 17983,
 "sensor": {
 "supplierSensorId": 31352,
 "manufacturer": "Nova Fitness",
 "description": "SDS011"
 }
 },
 "measurementValues": [
 {
 "type": "PM10",
 "value": 8.64,
 "unit": "MICROGRAMS_PER_CUBIC_METER"
 },
 {
 "type": "PM25",
 "value": 1.95,
 "unit": "MICROGRAMS_PER_CUBIC_METER"
 }
 ]
 },

 {
 "occurredAt": "2019-09-30T16:17:39",
 "installation": {
 "location": {
 "latitude": 51.044,
 "longitude": 13.746,
 "elevation": 115.3
 },
 "address": {
 "country": "DE",
 "province": null,
 "additionalAddressDetails": null
 },
 "supplier": "LUFTDATEN",
 "supplierInstallationId": 13208,
 "sensor": {
 "supplierSensorId": 36,
 "manufacturer": "Nova Fitness",
 "description": "SDS011"
 }
 },
 "measurementValues": [
 {
 "type": null,
 "value": 40.4,
 "unit": null
 }
 ]
 },
 */
