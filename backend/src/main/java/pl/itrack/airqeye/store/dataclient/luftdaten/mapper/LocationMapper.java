package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.measurement.entity.Location;
import pl.itrack.airqeye.store.measurement.adapters.mapper.DefaultMapperConfig;

@Mapper(config = DefaultMapperConfig.class)
interface LocationMapper {

  @Mapping(target = "elevation", source = "altitude")
  Location fromDto(pl.itrack.airqeye.store.dataclient.luftdaten.model.Location location);
}
