package pl.itrack.airqeye.store.measurement.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Country;
import pl.itrack.airqeye.store.measurement.domain.enumeration.Province;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {

  @Column(nullable = false)
  @Enumerated
  private Country country;

  @Enumerated
  private Province province;

  @Column(name = "ADDITIONAL_ADDRESS_DETAILS")
  private String additionalAddressDetails;
}
