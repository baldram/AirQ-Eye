package pl.itrack.airqeye.store.measurement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    @Column(name = "SUPPLIER_SENSOR_ID")
    private Long supplierSensorId;

    private String manufacturer;

    private String description;

}