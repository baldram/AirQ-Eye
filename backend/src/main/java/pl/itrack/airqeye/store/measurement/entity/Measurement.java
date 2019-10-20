package pl.itrack.airqeye.store.measurement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Measurement {

    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "OCCURRED_AT", nullable = false)
    private LocalDateTime occurredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_installation")
    private Installation installation;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "measurement", cascade = CascadeType.ALL)
    private List<MeasurementValue> measurementValues = new ArrayList<>();

    public Measurement() {
    }

    @Builder(toBuilder = true)
    public Measurement(Long id, LocalDateTime occurredAt, Installation installation,
                       List<MeasurementValue> measurementValues) {
        this.id = id;
        this.occurredAt = occurredAt;
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