package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO: Consider auto handling Jackson and builders, to use reflection to seek out the inner builder class.
 * https://stackoverflow.com/a/54363306/5394086
 * https://stackoverflow.com/questions/54350527/how-to-use-jackson-to-deserialize-external-lombok-builder-class
 * https://stackoverflow.com/a/51465038/5394086
 */

@Builder
@Value
@JsonDeserialize(builder = LuftdatenMeasurement.LuftdatenMeasurementBuilder.class)
public final class LuftdatenMeasurement {

    private final Long id;

    private final String timestamp;

    @NotNull
    private final Location location;

    private final Sensor sensor;

    @JsonProperty("sensordatavalues")
    private final List<SensorData> sensorData;

    // The default prefix for Jackson when use the builder to deserialize objects is "with".
    // The Lombok is configured to use the fluent builder (without any prefix).
    @JsonPOJOBuilder(withPrefix = "")
    public static final class LuftdatenMeasurementBuilder {
    }
}
