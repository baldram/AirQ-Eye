package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.measurement.mapper.DefaultMapperConfig;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.SensorData;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Country;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.disjoint;
import static pl.itrack.airqeye.store.dataclient.luftdaten.config.SupportedDataTypes.SUPPORTED_MEASUREMENT_TYPES;
import static pl.itrack.airqeye.store.dataclient.luftdaten.config.SupportedDataTypes.SUPPORTED_SENSORS;

@Mapper(config = DefaultMapperConfig.class,
        uses = {MeasurementValueMapper.class, InstallationMapper.class})
public interface MeasurementMapper {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    Predicate<LuftdatenMeasurement> WITH_SUPPORTED_COUNTRIES = entry -> Country.contains(entry.getLocation().getCountry());
    Predicate<LuftdatenMeasurement> WITH_SUPPORTED_SENSORS = entry -> SUPPORTED_SENSORS.contains(entry.getSensor().getType().getName());
    Predicate<LuftdatenMeasurement> WITH_SUPPORTED_VALUE_TYPES = entry -> !disjoint(getValueTypes(entry), SUPPORTED_MEASUREMENT_TYPES);

    static Set<String> getValueTypes(LuftdatenMeasurement entry) {
        return entry.getSensorData().stream().map(SensorData::getValueType).collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "occurredAt", source = "dataFeed.timestamp")
    @Mapping(target = "installation", source = "dataFeed")
    @Mapping(target = "measurementValues", source = "dataFeed.sensorData")
    Measurement fromDto(LuftdatenMeasurement dataFeed);

    default LocalDateTime fromDateString(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    // TODO: rename
    default List<Measurement> fromDtos(List<LuftdatenMeasurement> data) {
        Objects.requireNonNull(data);

        List<LuftdatenMeasurement> validData = getValidData(data);

        return validData
                .stream()
                .map(this::fromDto)
                .collect(Collectors.toCollection(() -> new ArrayList<>(validData.size())));
    }

    default List<LuftdatenMeasurement> getValidData(List<LuftdatenMeasurement> data) {
        return data.stream()
                .filter(WITH_SUPPORTED_COUNTRIES
                        .and(WITH_SUPPORTED_SENSORS)
                        .and(WITH_SUPPORTED_VALUE_TYPES))
                .collect(Collectors.toList());
    }
}