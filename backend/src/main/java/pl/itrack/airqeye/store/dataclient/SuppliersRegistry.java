package pl.itrack.airqeye.store.dataclient;

import java.util.List;
import org.springframework.stereotype.Component;
import pl.itrack.airqeye.store.measurement.service.HasUpdatableDataFeed;

/**
 * The component collects a list of available data suppliers.
 * <p>
 * Registering a new data supplier simple as providing a new adapter for HasUpdatableDataFeed. It
 * will be automatically detected and included in data processing. The are simply injected here by
 * framework.
 */
@Component
public class SuppliersRegistry {

  private List<HasUpdatableDataFeed> dataSuppliers;

  public SuppliersRegistry(List<HasUpdatableDataFeed> dataSuppliers) {
    this.dataSuppliers = dataSuppliers;
  }

  /**
   * Provides a list of all registered data suppliers.
   *
   * @return data provider list
   */
  public final List<HasUpdatableDataFeed> getRegisteredDataClients() {
    return dataSuppliers;
  }
}
