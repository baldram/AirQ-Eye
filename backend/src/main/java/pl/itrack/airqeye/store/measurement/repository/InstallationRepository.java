package pl.itrack.airqeye.store.measurement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.enumeration.Feeder;

@Transactional(readOnly = true)
public interface InstallationRepository extends JpaRepository<Installation, Long> {

  @Query("SELECT i FROM Installation i WHERE i.feeder = :feeder")
  List<Installation> findByProvider(@Param("feeder") Feeder feeder);

  @Query("SELECT i FROM Installation i WHERE i.feeder = :feeder"
      + " AND i.feederInstallationId = :id")
  Optional<Installation> findByProvider(@Param("feeder") Feeder feeder,
      @Param("id") Long installationId);

  @Query("SELECT MAX(m.occurredAtUtc) FROM Installation i JOIN i.measurements m"
      + " WHERE i.feeder = :feeder")
  Optional<LocalDateTime> getLatestUpdate(@Param("feeder") Feeder dataProvider);

  // TODO: @Modifying --> deleteByProvider

}
