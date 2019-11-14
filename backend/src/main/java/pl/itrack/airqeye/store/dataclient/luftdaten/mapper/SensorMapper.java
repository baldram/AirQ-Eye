package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.measurement.domain.model.Sensor;
import pl.itrack.airqeye.store.measurement.infrastructure.mapper.DefaultMapperConfig;

@Mapper(config = DefaultMapperConfig.class)
interface SensorMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "feederSensorId", source = "id")
  @Mapping(target = "manufacturer", source = "type.manufacturer")
  @Mapping(target = "description", source = "type.name")
  Sensor fromDto(pl.itrack.airqeye.store.dataclient.luftdaten.model.Sensor sensorData);
}
