package pl.itrack.airqeye.store.measurement.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sensor {

  @JsonIgnore
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "FEEDER_SENSOR_ID")
  private Long feederSensorId;

  private String manufacturer;

  private String description;

}
