package pl.itrack.airqeye.store.measurement.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeasurementProperties {

  private int updateFrequencyInMinutes;

  public MeasurementProperties(
      @Value("${airq-eye.update-frequency-in-minutes:10}") int updateFrequencyInMinutes) {
    this.updateFrequencyInMinutes = updateFrequencyInMinutes;
  }

  public int getUpdateFrequencyInMinutes() {
    return updateFrequencyInMinutes;
  }
}
