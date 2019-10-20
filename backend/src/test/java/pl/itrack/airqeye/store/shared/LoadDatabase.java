package pl.itrack.airqeye.store.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.repository.InstallationRepository;

import java.time.LocalDateTime;

import static pl.itrack.airqeye.store.MeasurementTestDataBuilder.prebuildInstallation;
import static pl.itrack.airqeye.store.MeasurementTestDataBuilder.prebuildMeasurement;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    public CommandLineRunner initDatabase(InstallationRepository installationRepository) {
        // Installation is a root, but for persistence we take installation from measurement,
        // as while the measurement constructing, JPA reference is set. We used to add new measurements
        // to the existing installation and then all references (related to bidirectional association) are updated.
        final Measurement measurement =
                prebuildMeasurement(LocalDateTime.now().minusYears(5))
                        .installation(prebuildInstallation(Supplier.LUFTDATEN).build())
                        .build();
        return args -> log.info("Preloading " + installationRepository.save(measurement.getInstallation()));
    }
}
