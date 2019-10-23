package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.Location;
import pl.itrack.airqeye.store.measurement.entity.Address;
import pl.itrack.airqeye.store.measurement.mapper.DefaultMapperConfig;

@Mapper(config = DefaultMapperConfig.class)
interface AddressMapper {

  // the ignored ones are not provided by Luftdaten
  @Mapping(target = "province", ignore = true)
  @Mapping(target = "additionalAddressDetails", ignore = true)
  Address fromDto(Location location);
}
