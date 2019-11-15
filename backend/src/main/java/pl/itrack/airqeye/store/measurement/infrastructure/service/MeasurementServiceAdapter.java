package pl.itrack.airqeye.store.measurement.infrastructure.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.domain.model.Installation;
import pl.itrack.airqeye.store.measurement.domain.model.Measurement;
import pl.itrack.airqeye.store.measurement.domain.service.InstallationNotFoundException;
import pl.itrack.airqeye.store.measurement.domain.service.MeasurementService;
import pl.itrack.airqeye.store.measurement.infrastructure.config.MeasurementPropertiesAdapter;
import pl.itrack.airqeye.store.measurement.infrastructure.repository.InstallationRepository;

@Service
@Transactional
public class MeasurementServiceAdapter implements MeasurementService {

  private InstallationRepository installationRepository;

  private MeasurementPropertiesAdapter measurementProperties;

  public MeasurementServiceAdapter(
      InstallationRepository installationRepository,
      MeasurementPropertiesAdapter properties) {
    this.installationRepository = installationRepository;
    this.measurementProperties = properties;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Measurement> retrieveMeasurements() {
    return installationRepository.findAll().stream()
        .map(Installation::getMeasurements)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Measurement> retrieveMeasurements(final Long stationId, final Feeder feeder) {
    Installation installation = installationRepository.findByProvider(feeder, stationId)
        .orElseThrow(() -> new InstallationNotFoundException(stationId));

    return installation.getMeasurements();
  }

  @Override
  public void refreshDataIfRequired(Supplier<List<Measurement>> dataFeed, Feeder dataProvider) {
    if (isUpdateRequired(dataProvider)) {
      List<Measurement> measurements = dataFeed.get();
      if (!measurements.isEmpty()) {
        removeData(dataProvider);
        persist(measurements);
      }
    }
  }

  /**
   * Persists measurements data.
   *
   * @param measurements - measurements data to be persisted
   * @return - installations including persisted measurements
   */
  List<Installation> persist(final List<Measurement> measurements) {
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
  void removeData(final Feeder dataProvider) {
    // FIXME: modify to delete by IDs in batch in partitions
    //  or try to implement delete with where clause by enum
    installationRepository.findByProvider(dataProvider)
        .forEach(installation -> installationRepository.deleteById(installation.getId()));
  }

  @Override
  @Transactional(readOnly = true)
  public LocalDateTime getLatestUpdate(final Feeder dataProvider) {
    final Optional<LocalDateTime> latestUpdate = installationRepository
        .getLatestUpdate(dataProvider);
    return latestUpdate.orElseGet(() -> LocalDateTime.of(1970, 1, 1, 0, 0, 0));
  }

  @Override
  public MeasurementPropertiesAdapter getMeasurementProperties() {
    return measurementProperties;
  }
}
