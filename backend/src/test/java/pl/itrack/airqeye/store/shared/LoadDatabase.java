package pl.itrack.airqeye.store.shared;

import static pl.itrack.airqeye.store.MeasurementTestDataBuilder.prebuildInstallation;
import static pl.itrack.airqeye.store.MeasurementTestDataBuilder.prebuildMeasurement;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.repository.InstallationRepository;

@Configuration
@Slf4j
public class LoadDatabase {

  /**
   * Loads the initial test data.
   *
   * @param installationRepository - the autowired repository for data persistence
   */
  @Bean
  public CommandLineRunner initDatabase(InstallationRepository installationRepository) {
    // Installation is a root, but for persistence we take installation from measurement,
    // as while the measurement constructing, JPA reference is set. We used to add new measurements
    // to the existing installation, further references (for bidirectional association) are updated.
    final Measurement measurement =
        prebuildMeasurement(LocalDateTime.now().minusYears(5))
            .installation(prebuildInstallation(Feeder.LUFTDATEN).build())
            .build();
    return args -> log
        .info("Preloading " + installationRepository.save(measurement.getInstallation()));
  }
}
