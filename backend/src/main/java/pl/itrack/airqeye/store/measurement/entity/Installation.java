package pl.itrack.airqeye.store.measurement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Installation {

  @JsonIgnore
  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "latitude", column = @Column(name = "LATITUDE")),
      @AttributeOverride(name = "longitude", column = @Column(name = "LONGITUDE")),
      @AttributeOverride(name = "elevation", column = @Column(name = "ELEVATION"))
  })
  private Location location;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "country", column = @Column(name = "COUNTRY")),
      @AttributeOverride(name = "province", column = @Column(name = "PROVINCE")),
      @AttributeOverride(name = "additionalAddressDetails",
          column = @Column(name = "ADDITIONAL_ADDRESS_DETAILS"))
  })
  private Address address;

  @Enumerated
  private Supplier supplier;

  @Column(name = "SUPPLIER_INSTALLATION_ID")
  private Long supplierInstallationId;

  @OneToOne(cascade = CascadeType.ALL)
  private Sensor sensor;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Builder.Default
  @JsonIgnore
  @OneToMany(mappedBy = "installation", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Measurement> measurements = new ArrayList<>();

}
