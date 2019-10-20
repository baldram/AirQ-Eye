package pl.itrack.airqeye.store.measurement.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;


@MapperConfig(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public class DefaultMapperConfig {
}
