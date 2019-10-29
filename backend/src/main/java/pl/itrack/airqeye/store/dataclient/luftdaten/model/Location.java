package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Location {

  private Long id;

  private double latitude;

  private double longitude;

  private double altitude;

  @NotNull
  private String country;

}
