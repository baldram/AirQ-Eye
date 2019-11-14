package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.measurement.domain.model.Installation;
import pl.itrack.airqeye.store.measurement.adapters.mapper.DefaultMapperConfig;

@Mapper(config = DefaultMapperConfig.class,
    uses = {LocationMapper.class, AddressMapper.class, SensorMapper.class})
interface InstallationMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "address", source = "location")
  @Mapping(target = "feeder", constant = "LUFTDATEN")
  @Mapping(target = "feederInstallationId", expression = "java(dataFeed.getLocation().getId())")
  @Mapping(target = "measurements", ignore = true)
  Installation fromDto(LuftdatenMeasurement dataFeed);
}
