package pl.itrack.airqeye.store.dataclient.luftdaten.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.itrack.airqeye.store.dataclient.luftdaten.LuftdatenClient;
import pl.itrack.airqeye.store.dataclient.luftdaten.config.DataFeedSettings;
import pl.itrack.airqeye.store.dataclient.luftdaten.mapper.MeasurementMapper;
import pl.itrack.airqeye.store.dataclient.luftdaten.model.LuftdatenMeasurement;
import pl.itrack.airqeye.store.measurement.entity.Measurement;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;
import pl.itrack.airqeye.store.measurement.service.HasUpdatableDataFeed;
import pl.itrack.airqeye.store.measurement.service.MeasurementService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LuftdatenService implements HasUpdatableDataFeed {

    @Autowired
    private LuftdatenClient luftdatenClient;

    @Autowired
    private MeasurementMapper measurementMapper;

    @Autowired
    private MeasurementService measurementService;

    // TODO: Data should be separately retrieved and persisted by some background job.
    @Override
    public void refreshDataIfRequired() {
        if (isUpdateRequired()) {
            List<Measurement> measurements = this.retrieveData();
            if (!measurements.isEmpty()) {
                measurementService.removeData(Supplier.LUFTDATEN);
                measurementService.persist(measurements);
            }
        }
    }

    private boolean isUpdateRequired() {
        LocalDateTime lastUpdate = measurementService.getLatestUpdate(Supplier.LUFTDATEN);
        return LocalDateTime.now().isAfter(lastUpdate.plusMinutes(DataFeedSettings.DATA_UPDATE_FREQUENCY_IN_MINUTES));
    }

    /**
     * Calls Luftdaten data feed and converts retrieved data to entities ready for persistence
     *
     * @return list of measurements
     */
    public List<Measurement> retrieveData() {
        List<LuftdatenMeasurement> luftdatenResponse = luftdatenClient.retrieveData().getBody();
        return measurementMapper.fromDtos(luftdatenResponse);
    }
}
