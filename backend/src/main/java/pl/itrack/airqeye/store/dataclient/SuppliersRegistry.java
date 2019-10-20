package pl.itrack.airqeye.store.dataclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.itrack.airqeye.store.dataclient.luftdaten.service.LuftdatenService;
import pl.itrack.airqeye.store.measurement.service.HasUpdatableDataFeed;

import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class SuppliersRegistry {

    @Autowired
    private LuftdatenService luftdatenService;

    public List<HasUpdatableDataFeed> getRegisteredDataClients() {
        return singletonList(luftdatenService);
    }
}
