package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor(staticName = "of")
public class SensorType {

  private Long id;

  private String manufacturer;

  private String name;

}
