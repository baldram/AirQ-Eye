package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor(staticName = "of")
public class SensorData {

  private Long id;

  private double value;

  @JsonProperty("value_type")
  private String valueType;

}
