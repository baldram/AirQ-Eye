package pl.itrack.airqeye.store.dataclient.luftdaten.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static pl.itrack.airqeye.store.measurement.enumeration.Feeder.LUFTDATEN;

import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.itrack.airqeye.store.dataclient.luftdaten.LuftdatenClient;
import pl.itrack.airqeye.store.dataclient.luftdaten.mapper.MeasurementMapper;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Feeder;
import pl.itrack.airqeye.store.measurement.service.MeasurementService;

@RunWith(MockitoJUnitRunner.class)
public class LuftdatenServiceTest {

  @Mock
  private LuftdatenClient luftdatenClient;

  @Mock
  private MeasurementMapper measurementMapper;

  @Mock
  private MeasurementService measurementService;

  @Captor
  private ArgumentCaptor<List<LuftdatenMeasurement>> luftdatenMeasurementsCaptor;

  @Captor
  private ArgumentCaptor<Supplier<List<Measurement>>> measurementSupplierCaptor;

  @Captor
  private ArgumentCaptor<Feeder> feederCaptor;

  @InjectMocks
  private LuftdatenService luftdatenService = new LuftdatenService(
      luftdatenClient, measurementMapper, measurementService);

  @Test
  public void retrieveData() {
    // Given
    final List<LuftdatenMeasurement> luftdatenMeasurements = getRetrievedDataMock();
    final List<Measurement> convertedMeasurements = getConvertedDataMock();

    // When
    final List<Measurement> result = luftdatenService.retrieveData();

    // Then
    assertThat(luftdatenMeasurementsCaptor.getValue()).isEqualTo(luftdatenMeasurements);
    assertThat(result).isEqualTo(convertedMeasurements);
  }

  private List<LuftdatenMeasurement> getRetrievedDataMock() {
    final List<LuftdatenMeasurement> luftdatenMeasurements = singletonList(
        LuftdatenMeasurement.builder().build());
    when(luftdatenClient.getMeasurements()).thenReturn(luftdatenMeasurements);
    return luftdatenMeasurements;
  }

  private List<Measurement> getConvertedDataMock() {
    final List<Measurement> convertedMeasurements = singletonList(Measurement.builder().build());
    when(measurementMapper.fromDtos(luftdatenMeasurementsCaptor.capture()))
        .thenReturn(convertedMeasurements);
    return convertedMeasurements;
  }

  /**
   * The LuftdatenService delegates refresh related check to the MeasurementService. This test
   * ensures whether all information is properly passed there.
   */
  @Test
  public void refreshDataIfRequired() {
    // Given
    final List<Measurement> convertedMeasurements = List.of(Measurement.builder().id(1L).build());
    when(measurementMapper.fromDtos(any())).thenReturn(convertedMeasurements);
    doNothing().when(measurementService)
        .refreshDataIfRequired(measurementSupplierCaptor.capture(), feederCaptor.capture());

    // When
    luftdatenService.refreshDataIfRequired();

    // Then
    assertThat(feederCaptor.getValue()).isEqualTo(LUFTDATEN);
    assertThat(measurementSupplierCaptor.getValue().get()).isEqualTo(convertedMeasurements);
  }

}
