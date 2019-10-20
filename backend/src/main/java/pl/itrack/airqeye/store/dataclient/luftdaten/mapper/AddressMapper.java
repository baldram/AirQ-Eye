package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.measurement.mapper.DefaultMapperConfig;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.Location;
import pl.itrack.airqeye.store.measurement.entity.Address;

@Mapper(config = DefaultMapperConfig.class)
interface AddressMapper {

    @Mapping(target = "province", ignore = true) // not provided by Luftdaten
    @Mapping(target = "additionalAddressDetails", ignore = true)  // not provided by Luftdaten
    Address fromDto(Location location);
}
