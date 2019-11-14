package pl.itrack.airqeye.store.measurement.repository;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.itrack.airqeye.store.shared.MeasurementTestDataBuilder.prebuildInstallation;
import static pl.itrack.airqeye.store.shared.MeasurementTestDataBuilder.prebuildMeasurement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.itrack.airqeye.store.measurement.infrastructure.repository.InstallationRepository;
import pl.itrack.airqeye.store.measurement.domain.model.Installation;
import pl.itrack.airqeye.store.measurement.domain.model.Measurement;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InstallationRepositoryITest {

  private static final long FEEDER_INSTALLATION_ID_1 = 1L;
  private static final long FEEDER_INSTALLATION_ID_2 = 2L;
  private static final long FEEDER_INSTALLATION_ID_3 = 3L;

  private static final LocalDateTime DATE_1
      = LocalDateTime.of(9999, 9, 11, 2, 3, 5);
  private static final LocalDateTime DATE_2
      = LocalDateTime.of(9999, 9, 13, 7, 11, 13);
  private static final LocalDateTime DATE_3
      = LocalDateTime.of(9999, 9, 17, 17, 19, 23);
  private static final LocalDateTime DATE_4
      = LocalDateTime.of(9999, 9, 19, 2, 3, 5);

  @Autowired
  private InstallationRepository repository;

  @Test
  public void findProviderRelatedData() {
    // Given
    buildAndPersistInstallations();

    // When
    List<Installation> result = repository.findByProvider(Feeder.LUFTDATEN);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(2);
  }

  private void buildAndPersistInstallations() {
    // clean start
    repository.deleteAll();

    // there are existing installations without any measurements yet
    final Installation installation1 = prebuildInstallation(Feeder.LUFTDATEN,
        FEEDER_INSTALLATION_ID_1).build();
    final Installation installation2 = prebuildInstallation(Feeder.LUFTDATEN,
        FEEDER_INSTALLATION_ID_2).build();
    final Installation installation3 = prebuildInstallation(Feeder.GIOS,
        FEEDER_INSTALLATION_ID_3).build();
    List<Installation> persistedInstallations = repository
        .saveAll(asList(installation1, installation2, installation3));

    // add new received measurements to existing installations
    final Measurement measurement1 = prebuildMeasurement(DATE_1)
        .installation(persistedInstallations.get(0)).build();
    final Measurement measurement2 = prebuildMeasurement(DATE_2)
        .installation(persistedInstallations.get(1)).build();
    final Measurement measurement3 = prebuildMeasurement(DATE_3)
        .installation(persistedInstallations.get(1)).build();
    final Measurement measurement4 = prebuildMeasurement(DATE_4)
        .installation(persistedInstallations.get(2)).build();
    List<Measurement> measurements = asList(measurement1, measurement2, measurement3, measurement4);

    // take all updated installations with measurements and JPA references set
    List<Installation> installations = measurements.stream().map(Measurement::getInstallation)
        .collect(toList());

    // update the installations to get new measurements persisted in the data base
    repository.saveAll(installations);
    repository.flush();
  }

  @Test
  public void findSelectedProviderRelatedData() {
    // Given
    buildAndPersistInstallations();

    // When
    Optional<Installation> result = repository
        .findByProvider(Feeder.LUFTDATEN, FEEDER_INSTALLATION_ID_2);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).isPresent();
    assertThat(result).hasValueSatisfying(value ->
        assertThat(value.getFeederInstallationId()).isEqualTo(FEEDER_INSTALLATION_ID_2));
  }

  @Test
  public void selectedProviderRelatedDataNotFound() {
    // Given
    buildAndPersistInstallations();

    // When
    Optional<Installation> result = repository.findByProvider(Feeder.LUFTDATEN, 666L);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  public void noProviderRelatedDataFound() {
    // Given
    buildAndPersistInstallations();

    // When
    List<Installation> result = repository.findByProvider(Feeder.AIRLY);

    // Then
    // only LUFTDATEN and GIOS data were persisted, so no data for AIRLY
    assertThat(result).isEmpty();
  }

  @Test
  public void getLatestUpdate() {
    // Given
    buildAndPersistInstallations();

    // When
    final Optional<LocalDateTime> determinedDate = repository.getLatestUpdate(Feeder.LUFTDATEN);

    // Then
    // Even DATE_4 is the latest one, it's not considered (it's other provider relevant).
    // The DATE_3 is then the chosen one as it's the latest Luftdaten related measurement.
    assertThat(determinedDate.orElse(null)).isEqualTo(DATE_3);
  }

  @Test
  public void lastUpdateNotDeterminedIfNoData() {
    // Given
    repository.deleteAll();

    // When
    final Optional<LocalDateTime> determinedDate = repository.getLatestUpdate(Feeder.LUFTDATEN);

    // Then
    assertThat(determinedDate.isPresent()).isFalse();
  }
}
