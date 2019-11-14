package pl.itrack.airqeye.store.measurement.domain.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Supplier;
import pl.itrack.airqeye.store.measurement.domain.config.MeasurementProperties;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.entity.Measurement;

/**
 * The main service handling the measurement related processes.
 */
public interface MeasurementService {

  /**
   * Retrieves measurements from all providers.
   *
   * @return a list of all measurements
   */
  List<Measurement> retrieveMeasurements();

  /**
   * Provides the latest measurements related to given feeders's installation.
   *
   * @param stationId - related installation
   * @param feeder    - related data provider
   * @return measurements
   */
  List<Measurement> retrieveMeasurements(Long stationId, Feeder feeder);

  /**
   * Performs data refresh if outdated
   *
   * @param dataFeed     - lazy data feed
   * @param dataProvider - data provider (feeder)
   */
  void refreshDataIfRequired(Supplier<List<Measurement>> dataFeed, Feeder dataProvider);

  /**
   * Checks whether data related to given feeder is outdated
   *
   * @param feeder - data provider
   * @return true if update is required
   */
  default boolean isUpdateRequired(Feeder feeder) {
    LocalDateTime lastUpdateUtc = getLatestUpdate(feeder);
    return LocalDateTime.now(ZoneOffset.UTC).isAfter(
        lastUpdateUtc.plusMinutes(getMeasurementProperties().getUpdateFrequencyInMinutes()));
  }

  /**
   * Finds the latest measurement date for given feeder.
   *
   * @param dataProvider - the feeder
   * @return date time of the last measurement
   */
  LocalDateTime getLatestUpdate(Feeder dataProvider);

  /**
   * Provides module settings specific for run environment
   *
   * @return measurement properties
   */
  MeasurementProperties getMeasurementProperties();
}
