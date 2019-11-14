package pl.itrack.airqeye.store.measurement.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.itrack.airqeye.store.measurement.domain.config.MeasurementProperties;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

  private static final LocalDateTime PAST_DATE = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
  private static final int DATA_REFRESH_RANGE = 10;

  @Mock
  private MeasurementService service;

  @BeforeEach
  void setUp() {
    final MeasurementProperties properties = mock(MeasurementProperties.class);
    when(service.isUpdateRequired(any())).thenCallRealMethod();
    when(properties.getUpdateFrequencyInMinutes()).thenReturn(10);
    when(service.getMeasurementProperties()).thenReturn(properties);
  }

  @Test
  @DisplayName("Determine that update in not required")
  void determineThatUpdateIsNotRequired() {
    // Given
    // Date on border of validity, but still refresh not required.
    // This test helps to verify whether time zone is considered while calculating validity.
    final LocalDateTime dateWithinValidRange = LocalDateTime.now(ZoneOffset.UTC)
        .minusMinutes(DATA_REFRESH_RANGE - 1);
    when(service.getLatestUpdate(any())).thenReturn(dateWithinValidRange);

    // When
    boolean result = service.isUpdateRequired(Feeder.AIRLY);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Determine that update necessary")
  void detectUpdateNecessity() {
    when(service.getLatestUpdate(any())).thenReturn(PAST_DATE);

    // When
    boolean result = service.isUpdateRequired(Feeder.AIRLY);

    // Then
    assertThat(result).isTrue();
  }
}
