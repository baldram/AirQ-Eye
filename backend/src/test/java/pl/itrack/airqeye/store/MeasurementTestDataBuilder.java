package pl.itrack.airqeye.store;

import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import pl.itrack.airqeye.store.measurement.entity.Address;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.entity.Location;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.entity.MeasurementValue;
import pl.itrack.airqeye.store.measurement.entity.Sensor;
import pl.itrack.airqeye.store.measurement.enumeration.Country;
import pl.itrack.airqeye.store.measurement.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.enumeration.Province;

public class MeasurementTestDataBuilder {

  private static final long FEEDER_INSTALLATION_ID = 149L;

  /**
   * Builds test data set with variable measurement date.
   *
   * @param occurredAt - measurement date time
   * @return prebuilt measurement data
   */
  public static Measurement.MeasurementBuilder prebuildMeasurement(LocalDateTime occurredAt) {

    final MeasurementValue measurementValue1 = getMeasurementValue(MeasurementType.PM10, 16.42);
    final MeasurementValue measurementValue2 = getMeasurementValue(MeasurementType.PM25, 11.55);

    return Measurement.builder()
        .occurredAtUtc(occurredAt)
        .measurementValues(asList(measurementValue1, measurementValue2));
  }

  private static MeasurementValue getMeasurementValue(MeasurementType type, double value) {
    return MeasurementValue.builder()
        .type(type)
        .value(value)
        .build();
  }

  public static Installation.InstallationBuilder prebuildInstallation(Feeder feeder) {
    return prebuildInstallation(feeder, FEEDER_INSTALLATION_ID);
  }

  /**
   * Builds installation data for given data provider and with variable feeder id.
   *
   * @param feeder               - given data feeder
   * @param feederInstallationId - installation external id from the feeder DB
   * @return prebuilt installation data
   */
  public static Installation.InstallationBuilder prebuildInstallation(Feeder feeder,
      Long feederInstallationId) {
    Address address = Address.builder()
        .country(Country.PL)
        .province(Province.POMORSKIE)
        .additionalAddressDetails("ul. Testowa 4B, Gdańsk") // np. Airly podaje
        .build();

    Sensor sensor = Sensor.builder()
        .feederSensorId(323L)
        .manufacturer("Nova Fitness")
        .description("SDS011") // name
        .build();

    return Installation.builder()
        .location(Location.builder().latitude(51.094).longitude(17.002).elevation(124).build())
        .address(address)
        .feeder(feeder)
        .feederInstallationId(feederInstallationId)
        .sensor(sensor);
  }
}
