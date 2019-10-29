package pl.itrack.airqeye.store.measurement.mapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements
    AttributeConverter<LocalDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(final LocalDateTime localDateTime) {
    return Optional.ofNullable(localDateTime).map(Timestamp::valueOf).orElse(null);
  }

  @Override
  public LocalDateTime convertToEntityAttribute(final Timestamp sqlTimestamp) {
    return Optional.ofNullable(sqlTimestamp).map(Timestamp::toLocalDateTime).orElse(null);
  }
}
