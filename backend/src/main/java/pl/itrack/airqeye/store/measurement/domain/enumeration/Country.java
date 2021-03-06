package pl.itrack.airqeye.store.measurement.domain.enumeration;

import java.util.stream.Stream;

public enum Country {
  AE, AL, AR, AT, AU, BA, BE, BG, BO, BY, CA, CD, CH, CL, CN, CR, CY, CZ, DE, DK, DO, ES, FI, FR,
  GB, GE, GF, GR, HK, HR, HU, ID, IE, IL, IN, IR, IT, JP, KE, KR, KW, KZ, LA, LI, LU, LV, MK, MX,
  MY, NL, NO, NZ, PH, PK, PL, PT, RO, RS, RU, SE, SG, SI, SK, TH, TR, UA, US, VI, VN, XK, ZA;

  /**
   * Checks whether the given value is a valid Country code.
   *
   * @param testedValue - given code to be tested
   * @return true if success, otherwise false
   */
  public static boolean contains(final String testedValue) {
    return Stream.of(values())
        .map(Enum::name)
        .anyMatch(code -> code.equals(testedValue));
  }
}
