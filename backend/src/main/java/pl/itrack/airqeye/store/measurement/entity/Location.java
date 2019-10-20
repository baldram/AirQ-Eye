package pl.itrack.airqeye.store.measurement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    /**
     * altitude at which device is installed MASL (meters above sea level)
     */
    private double elevation;
}
