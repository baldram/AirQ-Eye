package pl.itrack.airqeye.store.measurement.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.repository.InstallationRepository;

@Service
public class MeasurementService {

  @Autowired
  private InstallationRepository installationRepository;

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
   * Provides the latest measurements related to given supplier's installation.
   *
   * @param installationId - related installation
   * @param supplier - related data provider
   * @return measurements
   */
  @Transactional(readOnly = true)
  public List<Measurement> retrieveMeasurements(final Long installationId, Supplier supplier) {
    Installation installation = installationRepository.findByProvider(supplier, installationId)
        .orElseThrow(() -> new InstallationNotFoundException(installationId));

    return installation.getMeasurements();
  }

  /**
   * Persists measurements data.
   *
   * @param measurements - measurements data to be persisted
   * @return - installations including persisted measurements
   */
  @Transactional
  public List<Installation> persist(List<Measurement> measurements) {
    final List<Installation> installations = getInstallations(measurements);
    final List<Installation> persistedInstallations = installationRepository.saveAll(installations);
    installationRepository.flush();
    return persistedInstallations;
  }

  private List<Installation> getInstallations(List<Measurement> measurements) {
    return measurements.stream()
        .map(Measurement::getInstallation)
        .collect(Collectors.toList());
  }

  /**
   * Removes all data for given supplier.
   *
   * @param dataProvider - the supplier
   */
  @Transactional
  public void removeData(Supplier dataProvider) {
    // FIXME: modify to delete by IDs in batch in partitions
    //  or try to implement delete with where clause by enum
    installationRepository.findByProvider(dataProvider)
        .forEach(installation -> installationRepository.deleteById(installation.getId()));
  }

  /**
   * Finds the latest measurement date for given supplier.
   *
   * @param dataProvider - the supplier
   * @return date time of the last measurement
   */
  public LocalDateTime getLatestUpdate(Supplier dataProvider) {
    final Optional<LocalDateTime> latestUpdate = installationRepository
        .getLatestUpdate(dataProvider);
    return latestUpdate.orElseGet(() -> LocalDateTime.of(1970, 1, 1, 0, 0, 0));
  }
}
