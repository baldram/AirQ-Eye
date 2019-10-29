package pl.itrack.airqeye.store.measurement.service;

public class InstallationNotFoundException extends RuntimeException {

  InstallationNotFoundException(final Long id) {
    super("Could not find installation " + id);
  }
}
