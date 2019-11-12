package pl.itrack.airqeye.store.dataclient.luftdaten.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.dataclient.luftdaten.LuftdatenClient;
import pl.itrack.airqeye.store.dataclient.luftdaten.mapper.MeasurementMapper;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.service.HasUpdatableDataFeed;
import pl.itrack.airqeye.store.measurement.service.MeasurementService;

@Service
public class LuftdatenService implements HasUpdatableDataFeed {

  private LuftdatenClient luftdatenClient;

  private MeasurementMapper measurementMapper;

  private MeasurementService measurementService;

  public LuftdatenService(
      final LuftdatenClient luftdatenClient,
      final MeasurementMapper measurementMapper,
      final MeasurementService measurementService) {
    this.luftdatenClient = luftdatenClient;
    this.measurementMapper = measurementMapper;
    this.measurementService = measurementService;
  }

  // TODO: Data should be separately retrieved and persisted by some background job.
  @Override
  @Transactional
  public void refreshDataIfRequired() {
    java.util.function.Supplier<List<Measurement>> luftdatenFeed = this::retrieveData;
    measurementService.refreshDataIfRequired(luftdatenFeed, Supplier.LUFTDATEN);
  }

  /**
   * Calls Luftdaten data feed and converts retrieved data to entities ready for persistence.
   *
   * @return list of measurements
   */
  @Transactional(readOnly = true)
  List<Measurement> retrieveData() {
    return measurementMapper.fromDtos(luftdatenClient.getMeasurements());
  }
}
