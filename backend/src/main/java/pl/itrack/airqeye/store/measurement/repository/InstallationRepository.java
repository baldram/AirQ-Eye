package pl.itrack.airqeye.store.measurement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.itrack.airqeye.store.measurement.entity.Installation;
import pl.itrack.airqeye.store.measurement.enumeration.Supplier;

@Transactional(readOnly = true)
public interface InstallationRepository extends JpaRepository<Installation, Long> {

  @Query("SELECT i FROM Installation i WHERE i.supplier = :supplier")
  List<Installation> findByProvider(@Param("supplier") Supplier supplier);

  @Query("SELECT i FROM Installation i WHERE i.supplier = :supplier"
      + " AND i.supplierInstallationId = :id")
  Optional<Installation> findByProvider(@Param("supplier") Supplier supplier,
      @Param("id") Long installationId);

  @Query("SELECT MAX(m.occurredAtUtc) FROM Installation i JOIN i.measurements m"
      + " WHERE i.supplier = :supplier")
  Optional<LocalDateTime> getLatestUpdate(@Param("supplier") Supplier dataProvider);

  // TODO: @Modifying --> deleteByProvider

}
