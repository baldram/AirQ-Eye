package pl.itrack.airqeye.store.measurement.domain.service;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * The component collects a list of available data feeders.
 * <p>
 * Registering a new data feeder simple as providing a new adapter for HasUpdatableDataFeed. It will
 * be automatically detected and included in data processing. The are simply injected here by
 * framework.
 */
@Component
public class FeederRegistry {

  private List<HasUpdatableDataFeed> dataFeeders;

  public FeederRegistry(List<HasUpdatableDataFeed> dataFeeders) {
    this.dataFeeders = dataFeeders;
  }

  /**
   * Provides a list of all registered data feeders.
   *
   * @return data provider list
   */
  public final List<HasUpdatableDataFeed> getRegisteredDataClients() {
    return dataFeeders;
  }
}
