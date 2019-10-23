package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonDeserialize(builder = Location.LocationBuilder.class)
public class Location {

  private Long id;

  private double latitude;

  private double longitude;

  private double altitude;

  @NotNull
  private String country;

  @JsonPOJOBuilder(withPrefix = "")
  public static final class LocationBuilder {

  }
}
