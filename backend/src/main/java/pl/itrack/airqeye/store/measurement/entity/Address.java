package pl.itrack.airqeye.store.measurement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.itrack.airqeye.store.measurement.enumeration.Country;
import pl.itrack.airqeye.store.measurement.enumeration.Province;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

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
