package pl.itrack.airqeye.store.measurement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementType;
import pl.itrack.airqeye.store.measurement.enumeration.MeasurementUnit;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MEASUREMENT_VALUE")
public class MeasurementValue {

  @JsonIgnore
  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  @Enumerated
  private MeasurementType type;

  @Column(nullable = false)
  private double value;

  /**
   * code for eg.: µg/m³, °C
   */
  @Enumerated
  private MeasurementUnit unit;

  @JsonIgnore
  @ManyToOne
  private Measurement measurement;

}
