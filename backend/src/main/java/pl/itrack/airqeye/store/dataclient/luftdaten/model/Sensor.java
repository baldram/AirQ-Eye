package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class Sensor {

  private Long id;

  @NonNull
  @JsonProperty("sensor_type")
  private SensorType type;

}
