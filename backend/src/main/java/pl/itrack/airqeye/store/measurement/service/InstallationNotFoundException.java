package pl.itrack.airqeye.store.measurement.service;

public class InstallationNotFoundException extends RuntimeException {
    InstallationNotFoundException(Long id) {
        super("Could not find installation " + id);
    }
}
