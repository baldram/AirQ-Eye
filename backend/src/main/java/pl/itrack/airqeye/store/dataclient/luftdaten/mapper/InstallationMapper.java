package pl.itrack.airqeye.store.dataclient.luftdaten.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.mapper.DefaultMapperConfig;

@Mapper(config = DefaultMapperConfig.class,
        uses = {LocationMapper.class, AddressMapper.class, SensorMapper.class})
interface InstallationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", source = "location")
    @Mapping(target = "supplier", expression = "java(pl.itrack.airqeye.store.measurement.enumeration.Supplier.LUFTDATEN)")
    @Mapping(target = "supplierInstallationId", expression = "java((long) dataFeed.getLocation().getId())")
    @Mapping(target = "measurements", ignore = true)
    Installation fromDto(LuftdatenMeasurement dataFeed);
}
