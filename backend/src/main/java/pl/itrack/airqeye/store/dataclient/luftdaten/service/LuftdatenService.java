package pl.itrack.airqeye.store.dataclient.luftdaten.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.dataclient.luftdaten.LuftdatenClient;
import pl.itrack.airqeye.store.dataclient.luftdaten.mapper.MeasurementMapper;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.domain.service.HasUpdatableDataFeed;
import pl.itrack.airqeye.store.measurement.service.MeasurementService;

@Transactional
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

  /**
   * Calls Luftdaten data feed and converts retrieved data to entities ready for persistence.
   *
   * @return list of measurements
   */
  @Override
  @Transactional(readOnly = true)
  public List<Measurement> retrieveData() {
    return measurementMapper.fromDtos(luftdatenClient.getMeasurements());
  }

  @Override
  public MeasurementService getMeasurementService() {
    return measurementService;
  }

  @Override
  public Feeder getFeederType() {
    return Feeder.LUFTDATEN;
  }
}
