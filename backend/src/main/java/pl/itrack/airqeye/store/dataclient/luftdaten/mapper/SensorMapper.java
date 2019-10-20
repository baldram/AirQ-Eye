package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.measurement.mapper.DefaultMapperConfig;
import pl.itrack.airqeye.store.measurement.entity.Sensor;

@Mapper(config = DefaultMapperConfig.class)
interface SensorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplierSensorId", source = "id")
    @Mapping(target = "manufacturer", source = "type.manufacturer")
    @Mapping(target = "description", source = "type.name")
    Sensor fromDto(pl.itrack.airqeye.store.dataclient.luftdaten.model.Sensor sensorData);
}