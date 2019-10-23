package pl.itrack.airqeye.store.measurement.entity;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;

/**
 * Test one-to-many association whether objects are correctly linked. Check whether child table
 * record references the primary key of the parent table row correctly.
 */
@DisplayName("Test bidirectional @OneToMany association - if child references the id of the parent")
class MeasurementTest {

  private static final long INSTALLATION_ID = 11L;
  private static final long MEASUREMENT_ID = 22L;
  private static final LocalDateTime OCCURRED_AT = LocalDateTime.now();
  private static final double VALUE_1 = 666d;
  private static final double VALUE_2 = 777d;
  private static final Supplier SUPPLIER = Supplier.AIRLY;

  @Test
  @DisplayName("Handle circular references")
  void handleCircularReferences() {
    // Given
    final MeasurementValue measurementValue1 = MeasurementValue.builder().value(VALUE_1).build();
    final MeasurementValue measurementValue2 = MeasurementValue.builder().value(VALUE_2).build();
    final Installation installation = Installation.builder().id(INSTALLATION_ID).supplier(SUPPLIER)
        .build();

    // When
    final Measurement measurement = Measurement.builder()
        .id(MEASUREMENT_ID)
        .occurredAtUtc(OCCURRED_AT)
        .measurementValues(asList(measurementValue1, measurementValue2))
        .installation(installation)
        .build();

    // Then
    assertThat(measurement.getOccurredAtUtc()).isEqualTo(OCCURRED_AT);
    assertThat(measurement.getId()).isEqualTo(MEASUREMENT_ID);
    assertThat(measurement.getInstallation().getSupplier()).isEqualTo(SUPPLIER);
    assertThat(measurement.getInstallation().getMeasurements()).containsOnly(measurement);
    assertThat(measurement.getMeasurementValues()).isNotNull();
    assertThat(measurement.getMeasurementValues()).hasSize(2);
    assertThat(measurement.getMeasurementValues().get(0).getMeasurement().getId())
        .isEqualTo(MEASUREMENT_ID);
    assertThat(measurement.getMeasurementValues().get(1).getMeasurement().getId())
        .isEqualTo(MEASUREMENT_ID);
    assertThat(measurement.getMeasurementValues().stream().map(MeasurementValue::getValue))
        .containsExactlyInAnyOrder(VALUE_1, VALUE_2);
  }

  @Test
  @DisplayName("Provide an empty collection if no associated objects")
  void useEmptyCollectionIfNoAssociatedObjects() {
    // Given-When
    final Measurement measurement = Measurement.builder().occurredAtUtc(OCCURRED_AT).build();

    // Then
    assertThat(measurement.getOccurredAtUtc()).isEqualTo(OCCURRED_AT);
    assertThat(measurement.getMeasurementValues()).isNotNull();
    assertThat(measurement.getMeasurementValues()).isEmpty();
  }
}
