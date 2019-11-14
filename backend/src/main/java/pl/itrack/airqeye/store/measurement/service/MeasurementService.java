package pl.itrack.airqeye.store.measurement.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.measurement.adapters.config.MeasurementProperties;
import pl.itrack.airqeye.store.measurement.domain.service.InstallationNotFoundException;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.repository.InstallationRepository;

@Service
public class MeasurementService {

  private InstallationRepository installationRepository;

  private MeasurementProperties measurementProperties;

  public MeasurementService(
      InstallationRepository installationRepository,
      MeasurementProperties properties) {
    this.installationRepository = installationRepository;
    this.measurementProperties = properties;
  }

  /**
   * Retrieves measurements from all providers.
   *
   * @return a list of all measurements
   */
  @Transactional(readOnly = true)
  public List<Measurement> retrieveMeasurements() {
    return installationRepository.findAll().stream()
        .map(Installation::getMeasurements)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * Provides the latest measurements related to given feeders's installation.
   *
   * @param stationId - related installation
   * @param feeder    - related data provider
   * @return measurements
   */
  @Transactional(readOnly = true)
  public List<Measurement> retrieveMeasurements(final Long stationId, final Feeder feeder) {
    Installation installation = installationRepository.findByProvider(feeder, stationId)
        .orElseThrow(() -> new InstallationNotFoundException(stationId));

    return installation.getMeasurements();
  }

  @Transactional
  public void refreshDataIfRequired(Supplier<List<Measurement>> dataFeed, Feeder dataProvider) {
    if (isUpdateRequired(dataProvider)) {
      List<Measurement> measurements = dataFeed.get();
      if (!measurements.isEmpty()) {
        removeData(dataProvider);
        persist(measurements);
      }
    }
  }

  private boolean isUpdateRequired(Feeder feeder) {
    LocalDateTime lastUpdateUtc = getLatestUpdate(feeder);
    return LocalDateTime.now(ZoneOffset.UTC)
        .isAfter(lastUpdateUtc.plusMinutes(measurementProperties.getUpdateFrequencyInMinutes()));
  }

  /**
   * Persists measurements data.
   *
   * @param measurements - measurements data to be persisted
   * @return - installations including persisted measurements
   */
  @Transactional
  public List<Installation> persist(final List<Measurement> measurements) {
    final List<Installation> installations = getInstallations(measurements);
    final List<Installation> persistedInstallations = installationRepository.saveAll(installations);
    installationRepository.flush();
    return persistedInstallations;
  }

  private List<Installation> getInstallations(final List<Measurement> measurements) {
    return measurements.stream()
        .map(Measurement::getInstallation)
        .collect(Collectors.toList());
  }

  /**
   * Removes all data for given feeder.
   *
   * @param dataProvider - the feeder
   */
  @Transactional
  public void removeData(final Feeder dataProvider) {
    // FIXME: modify to delete by IDs in batch in partitions
    //  or try to implement delete with where clause by enum
    installationRepository.findByProvider(dataProvider)
        .forEach(installation -> installationRepository.deleteById(installation.getId()));
  }

  /**
   * Finds the latest measurement date for given feeder.
   *
   * @param dataProvider - the feeder
   * @return date time of the last measurement
   */
  @Transactional(readOnly = true)
  public LocalDateTime getLatestUpdate(final Feeder dataProvider) {
    final Optional<LocalDateTime> latestUpdate = installationRepository
        .getLatestUpdate(dataProvider);
    return latestUpdate.orElseGet(() -> LocalDateTime.of(1970, 1, 1, 0, 0, 0));
  }
}
