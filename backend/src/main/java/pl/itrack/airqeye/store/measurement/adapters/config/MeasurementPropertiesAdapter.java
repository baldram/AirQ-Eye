package pl.itrack.airqeye.store.measurement.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import pl.itrack.airqeye.store.measurement.domain.config.MeasurementProperties;

@Configuration
public class MeasurementPropertiesAdapter implements MeasurementProperties {

  private int updateFrequencyInMinutes;

  public MeasurementPropertiesAdapter(
      @Value("${airq-eye.update-frequency-in-minutes:10}") int updateFrequencyInMinutes) {
    this.updateFrequencyInMinutes = updateFrequencyInMinutes;
  }

  @Override
  public int getUpdateFrequencyInMinutes() {
    return updateFrequencyInMinutes;
  }
}
