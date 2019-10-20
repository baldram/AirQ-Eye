package pl.itrack.airqeye.store.measurement.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.repository.InstallationRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeasurementServiceTest {

    @Mock
    private InstallationRepository installationRepository;

    @Captor
    private ArgumentCaptor<Long> installationIdCapture;

    @InjectMocks
    private MeasurementService measurementService = new MeasurementService();

    @Test
    public void getLatestUpdate() {
        // Given
        final LocalDateTime lastUpdateTime = LocalDateTime.now();
        when(installationRepository.getLatestUpdate(any(Supplier.class))).thenReturn(Optional.of(lastUpdateTime));

        // When
        final LocalDateTime result = measurementService.getLatestUpdate(Supplier.LUFTDATEN);

        // Then
        assertThat(result).isEqualTo(lastUpdateTime);
    }

    @Test
    public void retrieveMeasurements() {
        // Given
        final List<Measurement> measurements = singletonList(Measurement.builder().id(1L).build());
        final List<Installation> installations = singletonList(Installation.builder().measurements(measurements).build());

        when(installationRepository.findAll()).thenReturn(installations);

        // When
        final List<Measurement> result = measurementService.retrieveMeasurements();

        // Then
        assertThat(result).isEqualTo(measurements);
    }

    @Test
    public void removeData() {
        // Given
        final Supplier dataProvider = Supplier.LUFTDATEN;
        final Installation installation = Installation.builder().id(666L).build();
        when(installationRepository.findByProvider(eq(dataProvider))).thenReturn(Collections.singletonList(installation));

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
        final List<Installation> result = measurementService.persist(unpersistedInstallation.getMeasurements());

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
}