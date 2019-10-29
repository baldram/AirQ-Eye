package pl.itrack.airqeye.store.measurement;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.itrack.airqeye.store.dataclient.SuppliersRegistry;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.service.HasUpdatableDataFeed;
import pl.itrack.airqeye.store.measurement.service.MeasurementService;

@RestController
public class MeasurementsController {

  private static final String URI_MEASUREMENTS = "/measurements";
  private static final String URI_SELECTED_MEASUREMENTS =
      URI_MEASUREMENTS + "/{supplier}/{supplierInstallationId}";

  @Autowired
  private SuppliersRegistry suppliersRegistry;

  @Autowired
  private MeasurementService measurementService;

  /**
   * Retrieves and provides data previously persisted in DB from different providers mixed together,
   * to be further displayed and filtered according to user needs.
   *
   * @return the latest measurements from all providers
   */
  @GetMapping(URI_MEASUREMENTS)
  public List<Measurement> getMeasurements() {
    suppliersRegistry.getRegisteredDataClients()
        .forEach(HasUpdatableDataFeed::refreshDataIfRequired);

    return measurementService.retrieveMeasurements();
  }

  /**
   * Provides the latest measurements related to given supplier's installation.
   *
   * @param supplierInstallationId - supplier's installation id
   * @param supplier - supplier indication
   * @return the latest measurements related to given installation
   */
  @GetMapping(URI_SELECTED_MEASUREMENTS)
  public List<Measurement> getMeasurement(@PathVariable final Long supplierInstallationId,
      @PathVariable final Supplier supplier) {
    suppliersRegistry.getRegisteredDataClients()
        .forEach(HasUpdatableDataFeed::refreshDataIfRequired);

    return measurementService.retrieveMeasurements(supplierInstallationId, supplier);
  }
}
