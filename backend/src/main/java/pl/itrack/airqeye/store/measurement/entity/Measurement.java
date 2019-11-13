package pl.itrack.airqeye.store.measurement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class Measurement {

  @JsonIgnore
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "OCCURRED_AT_UTC", nullable = false)
  private LocalDateTime occurredAtUtc;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_installation")
  private Installation installation;

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @OneToMany(mappedBy = "measurement", cascade = CascadeType.ALL)
  private List<MeasurementValue> measurementValues = new ArrayList<>();

  public Measurement() {
  }

  /**
   * Constructs the Measurement object and updates references (for bidirectional association).
   *
   * @param id                - DB id
   * @param occurredAtUtc     - date time of the measurement
   * @param installation      - related installation
   * @param measurementValues - measured values
   */
  @Builder(toBuilder = true)
  public Measurement(final Long id, LocalDateTime occurredAtUtc, final Installation installation,
      final List<MeasurementValue> measurementValues) {
    this.id = id;
    this.occurredAtUtc = occurredAtUtc;
    // set association including parent reference
    if (measurementValues != null) {
      this.measurementValues = measurementValues;
      this.measurementValues.forEach(value -> value.setMeasurement(this));
    }
    if (installation != null) {
      installation.getMeasurements().add(this);
      this.installation = installation;
    }
  }
}
