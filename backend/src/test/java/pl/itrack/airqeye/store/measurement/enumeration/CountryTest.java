package pl.itrack.airqeye.store.measurement.enumeration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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