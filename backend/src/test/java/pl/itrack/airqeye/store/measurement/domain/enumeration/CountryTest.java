package pl.itrack.airqeye.store.measurement.domain.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CountryTest {

  @Test
  void confirmValidity() {
    assertThat(Country.contains("PL")).isTrue();
  }

  @Test
  void detectInvalidity() {
    assertThat(Country.contains("XX")).isFalse();
  }

  @Test
  void detectInvalidityEvenValuePartiallyValid() {
    assertThat(Country.contains("PLX")).isFalse();
  }
}
