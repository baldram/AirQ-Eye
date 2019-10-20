package pl.itrack.airqeye.store;

import pl.itrack.airqeye.store.measurement.entity.Address;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.entity.Location;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.entity.MeasurementValue;
import pl.itrack.airqeye.store.measurement.entity.Sensor;
import pl.itrack.airqeye.store.measurement.enumeration.Country;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.enumeration.Province;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;

public class MeasurementTestDataBuilder {

    private static final long SUPPLIER_INSTALLATION_ID = 149L;

    public static Measurement.MeasurementBuilder prebuildMeasurement(LocalDateTime occurredAt) {

        final MeasurementValue measurementValue1 = getMeasurementValue(MeasurementType.PM10, 16.42);
        final MeasurementValue measurementValue2 = getMeasurementValue(MeasurementType.PM25, 11.55);

        return Measurement.builder()
                .occurredAt(occurredAt)
                .measurementValues(asList(measurementValue1, measurementValue2));
    }

    private static MeasurementValue getMeasurementValue(MeasurementType type, double value) {
        return MeasurementValue.builder()
                .type(type)
                .value(value)
                .build();
    }

    public static Installation.InstallationBuilder prebuildInstallation(Supplier supplier) {
        return prebuildInstallation(supplier, SUPPLIER_INSTALLATION_ID);
    }

    public static Installation.InstallationBuilder prebuildInstallation(Supplier supplier, Long supplierInstallationId) {
        Address address = Address.builder()
                .country(Country.PL)
                .province(Province.POMORSKIE)
                .additionalAddressDetails("ul. Testowa 4B, Gdańsk") // np. Airly podaje
                .build();

        Sensor sensor = Sensor.builder()
                .supplierSensorId(323L)
                .manufacturer("Nova Fitness")
                .description("SDS011") // name
                .build();

        return Installation.builder()
                .location(Location.builder().latitude(51.094).longitude(17.002).elevation(124).build())
                .address(address)
                .supplier(supplier)
                .supplierInstallationId(supplierInstallationId)
                .sensor(sensor);
    }
}
