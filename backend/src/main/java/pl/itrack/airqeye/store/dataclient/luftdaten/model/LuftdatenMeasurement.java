package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public final class LuftdatenMeasurement {

  private final Long id;

  @JsonProperty("timestamp")
  private final String timestampUtc;

  @NotNull
  private final Location location;

  private final Sensor sensor;

  @JsonProperty("sensordatavalues")
  private final List<SensorData> sensorData;

}
