package pl.itrack.airqeye.store.dataclient.luftdaten.config;

import static java.util.Arrays.asList;

import java.util.List;

public class SupportedDataTypesHelper {

  /**
   * The list of supported sensor types as defined by Luftdaten. See "pm_sensors here:
   * https://github.com/opendata-stuttgart/feinstaub-map-v2/blob/master/src/js/feinstaub-api.js
   */
  public static final List<String> SUPPORTED_SENSORS =
      asList("SDS011", "SDS021", "PMS1003", "PMS3003",
          "PMS5003", "PMS6003", "PMS7003", "HPM", "SPS30");

  /**
   * Type names as provided by Luftdaten: https://maps.luftdaten.info/data/v2/data.dust.min.json
   * data source. Supported are sensors for particles with a diameter of 10 µm and below as well as
   * for particles less than 2.5 µm.
   */
  public static final String MEASUREMENT_TYPE_PM10 = "P1";
  public static final String MEASUREMENT_TYPE_PM2_5 = "P2";
  public static final List<String> SUPPORTED_MEASUREMENT_TYPES =
      asList(MEASUREMENT_TYPE_PM10, MEASUREMENT_TYPE_PM2_5);

}
