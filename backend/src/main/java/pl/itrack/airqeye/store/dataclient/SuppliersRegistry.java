package pl.itrack.airqeye.store.dataclient;

import static java.util.Collections.singletonList;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.itrack.airqeye.store.dataclient.luftdaten.service.LuftdatenService;
import pl.itrack.airqeye.store.measurement.service.HasUpdatableDataFeed;

@Component
public class SuppliersRegistry {

  @Autowired
  private LuftdatenService luftdatenService;

  public final List<HasUpdatableDataFeed> getRegisteredDataClients() {
    return singletonList(luftdatenService);
  }
}
