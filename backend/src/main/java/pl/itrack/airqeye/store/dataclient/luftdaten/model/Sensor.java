package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
@JsonDeserialize(builder = Sensor.SensorBuilder.class)
public class Sensor {

  private Long id;

  @NonNull
  @JsonProperty("sensor_type")
  private SensorType type;

  @JsonPOJOBuilder(withPrefix = "")
  public static final class SensorBuilder {

  }
}
