package pl.itrack.airqeye.store.measurement.infrastructure.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.domain.model.Installation;
import pl.itrack.airqeye.store.measurement.domain.model.Measurement;
import pl.itrack.airqeye.store.measurement.domain.service.InstallationNotFoundException;
import pl.itrack.airqeye.store.measurement.infrastructure.config.MeasurementPropertiesAdapter;
import pl.itrack.airqeye.store.measurement.infrastructure.repository.InstallationRepository;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementServiceAdapterTest {

  private static final long INSTALLATION_ID = 123L;
  private static final long NOT_EXISTING_INSTALLATION_ID = 666L;
  private static final String NOT_EXISTING_INSTALLATION_MESSAGE = "Could not find installation %s";
  private static final int DATA_REFRESH_RANGE = 10;
  private static final LocalDateTime PAST_DATE = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

  @Mock
  private InstallationRepository installationRepository;

  @Mock
  private MeasurementPropertiesAdapter measurementProperties;

  @Captor
  private ArgumentCaptor<Long> installationIdCapture;

  @Captor
  private ArgumentCaptor<List<Installation>> installationsCaptor;

  @Captor
  private ArgumentCaptor<Feeder> feederCaptor;

  @InjectMocks
  private MeasurementServiceAdapter measurementService =
      new MeasurementServiceAdapter(installationRepository, measurementProperties);

  @Before
  public void setUp() {
    when(measurementProperties.getUpdateFrequencyInMinutes()).thenReturn(DATA_REFRESH_RANGE);
  }

  @Test
  public void getLatestUpdate() {
    // Given
    final LocalDateTime lastUpdateTime = LocalDateTime.now();
    when(installationRepository.getLatestUpdate(any(Feeder.class)))
        .thenReturn(Optional.of(lastUpdateTime));

    // When
    final LocalDateTime result = measurementService.getLatestUpdate(Feeder.LUFTDATEN);

    // Then
    assertThat(result).isEqualTo(lastUpdateTime);
  }

  @Test
  public void retrieveMeasurements() {
    // Given
    final List<Measurement> measurements = singletonList(Measurement.builder().id(1L).build());
    final List<Installation> installations = singletonList(
        Installation.builder().measurements(measurements).build());

    when(installationRepository.findAll()).thenReturn(installations);

    // When
    final List<Measurement> result = measurementService.retrieveMeasurements();

    // Then
    assertThat(result).isEqualTo(measurements);
  }

  @Test
  public void retrieveMeasurementsForSpecifiedInstallation() {
    // Given
    final List<Measurement> measurements = singletonList(Measurement.builder().id(1L).build());
    final Optional<Installation> installation = Optional.of(Installation.builder()
        .feederInstallationId(INSTALLATION_ID).measurements(measurements).build());
    when(installationRepository.findByProvider(eq(Feeder.LUFTDATEN), eq(INSTALLATION_ID)))
        .thenReturn(installation);

    // When
    final List<Measurement> result = measurementService
        .retrieveMeasurements(INSTALLATION_ID, Feeder.LUFTDATEN);

    // Then
    assertThat(result).isEqualTo(measurements);
  }

  @Test
  public void throwErrorIfSpecifiedInstallationNotFound() {
    // When
    final Throwable expectedError = catchThrowable(() ->
        measurementService.retrieveMeasurements(NOT_EXISTING_INSTALLATION_ID, Feeder.LUFTDATEN));

    // Then
    assertThat(expectedError).isInstanceOf(InstallationNotFoundException.class)
        .hasMessageContaining(
            String.format(NOT_EXISTING_INSTALLATION_MESSAGE, NOT_EXISTING_INSTALLATION_ID));
  }

  @Test
  public void removeData() {
    // Given
    final Feeder dataProvider = Feeder.LUFTDATEN;
    final Installation installation = Installation.builder().id(1L).build();
    when(installationRepository.findByProvider(eq(dataProvider)))
        .thenReturn(singletonList(installation));

    // When
    measurementService.removeData(dataProvider);

    // Then
    verify(installationRepository).deleteById(installationIdCapture.capture());
    assertThat(installationIdCapture.getValue()).isEqualTo(installation.getId());
  }

  @Test
  public void persistNewMeasurements() {
    // Given
    final Installation unpersistedInstallation = buildInstallationWithMeasurementOfId(null);
    final Installation persistedInstallation = buildInstallationWithMeasurementOfId(1L);
    when(installationRepository.saveAll(eq(singletonList(unpersistedInstallation))))
        .thenReturn(singletonList(persistedInstallation));

    // When
    final List<Installation> result = measurementService
        .persist(unpersistedInstallation.getMeasurements());

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMeasurements()).hasSize(1);
    assertThat(result.get(0).getMeasurements()).isEqualTo(persistedInstallation.getMeasurements());
    verify(installationRepository).saveAll(eq(singletonList(unpersistedInstallation)));
    verify(installationRepository).flush();
  }

  private Installation buildInstallationWithMeasurementOfId(Long id) {
    final Installation installation = Installation.builder().build();
    final Measurement measurement = Measurement.builder().id(id).installation(installation).build();
    return installation.toBuilder().measurements(singletonList(measurement)).build();
  }

  @Test
  public void updateIfOutdatedMeasurements() {
    // Given
    mockLatestUpdateDateResponse(PAST_DATE);

    final Installation installation = Installation.builder().id(3L).build();
    final Supplier<List<Measurement>> measurementSupplier = () -> List
        .of(Measurement.builder().installation(installation).build());
    when(installationRepository.findByProvider(eq(Feeder.LUFTDATEN)))
        .thenReturn(List.of(installation));

    // When
    measurementService.refreshDataIfRequired(measurementSupplier, Feeder.LUFTDATEN);

    // Then
    verify(installationRepository).findByProvider(feederCaptor.capture());
    verify(installationRepository).deleteById(eq(installation.getId()));
    verify(installationRepository).saveAll(installationsCaptor.capture());
    assertThat(feederCaptor.getValue()).isEqualTo(Feeder.LUFTDATEN);
    assertThat(installationsCaptor.getValue()).hasSize(1);
    assertThat(installationsCaptor.getValue()).isEqualTo(singletonList(installation));
  }

  private void mockLatestUpdateDateResponse(LocalDateTime dateInValidRange) {
    when(installationRepository.getLatestUpdate(eq(Feeder.LUFTDATEN)))
        .thenReturn(Optional.of(dateInValidRange));
  }

  @Test
  public void noUpdateRequiredIfActualData() {
    // Given
    final LocalDateTime dateWithinValidRange = LocalDateTime.now(ZoneOffset.UTC)
        .minusMinutes(DATA_REFRESH_RANGE - 1);
    mockLatestUpdateDateResponse(dateWithinValidRange);

    final Installation installation = Installation.builder().id(3L).build();
    final Supplier<List<Measurement>> measurementSupplier = () -> List
        .of(Measurement.builder().installation(installation).build());

    // When
    measurementService.refreshDataIfRequired(measurementSupplier, Feeder.LUFTDATEN);

    // Then
    verify(installationRepository, never()).findByProvider(any());
    verify(installationRepository, never()).deleteById(eq(installation.getId()));
    verify(installationRepository, never()).saveAll(any());
  }

}
