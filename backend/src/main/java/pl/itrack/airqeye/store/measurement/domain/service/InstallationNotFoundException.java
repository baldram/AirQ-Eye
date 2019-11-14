package pl.itrack.airqeye.store.measurement.domain.service;

public class InstallationNotFoundException extends RuntimeException {

  public InstallationNotFoundException(final Long id) {
    super("Could not find installation " + id);
  }
}
