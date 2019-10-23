package pl.itrack.airqeye.store.measurement.enumeration;

import java.util.Arrays;

public enum Country {
  AE, AL, AR, AT, AU, BA, BE, BG, BO, BY, CA, CD, CH, CL, CN, CR, CY, CZ, DE, DK, DO, ES, FI, FR, GB, GE, GF, GR, HK,
  HR, HU, ID, IE, IL, IN, IR, IT, JP, KE, KR, KW, KZ, LA, LI, LU, LV, MK, MX, MY, NL, NO, NZ, PH, PK, PL, PT, RO, RS,
  RU, SE, SG, SI, SK, TH, TR, UA, US, VI, VN, XK, ZA;

  public static boolean contains(String testedValue) {
    return Arrays.stream(values())
        .map(Enum::name)
        .anyMatch(code -> code.equals(testedValue));
  }
}
