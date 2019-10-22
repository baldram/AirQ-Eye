package pl.itrack.airqeye.store.measurement;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.itrack.airqeye.store.measurement.service.InstallationNotFoundException;

@RestControllerAdvice
public class InstallationNotFoundAdvice {

    @ExceptionHandler(InstallationNotFoundException.class)
    public ResponseEntity<ErrorMessage> installationNotFoundHandler(InstallationNotFoundException exception) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ErrorMessage(exception.getMessage()));
    }

    @Value
    private class ErrorMessage {
        private String error;
    }
}
