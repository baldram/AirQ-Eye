package pl.itrack.airqeye.store.measurement.domain.service;

import java.util.List;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.entity.Measurement;

public interface HasUpdatableDataFeed {

  default void refreshDataIfRequired() {
    getMeasurementService().refreshDataIfRequired(this::retrieveData, getFeederType());
  }

  List<Measurement> retrieveData();

  MeasurementService getMeasurementService();

  Feeder getFeederType();
}
