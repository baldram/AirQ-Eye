package pl.itrack.airqeye.store.dataclient.luftdaten.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor(staticName = "of")
@JsonDeserialize(builder = SensorType.SensorTypeBuilder.class)
public class SensorType {

    private Long id;

    private String manufacturer, name;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class SensorTypeBuilder {
    }
}
