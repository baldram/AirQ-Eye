package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor(staticName = "of")
@JsonDeserialize(builder = SensorData.SensorDataBuilder.class)
public class SensorData {

  private Long id;

  private double value;

  @JsonProperty("value_type")
  private String valueType;

  @JsonPOJOBuilder(withPrefix = "")
  public static final class SensorDataBuilder {

  }
}
