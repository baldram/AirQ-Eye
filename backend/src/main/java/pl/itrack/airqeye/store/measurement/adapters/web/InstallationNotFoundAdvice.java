package pl.itrack.airqeye.store.measurement.adapters.web;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.itrack.airqeye.store.measurement.domain.service.InstallationNotFoundException;

@RestControllerAdvice
public class InstallationNotFoundAdvice {

  /**
   * Handles invalid REST requests.
   *
   * @param exception - an exception to be handled
   * @return error details
   */
  @ExceptionHandler(InstallationNotFoundException.class)
  public final ResponseEntity<ErrorMessage> installationNotFoundHandler(
      final InstallationNotFoundException exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorMessage(exception.getMessage()));
  }

  @Value
  private static class ErrorMessage {

    private String error;
  }
}
