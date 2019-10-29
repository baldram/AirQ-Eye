package pl.itrack.airqeye.store;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.i18n.LocaleContextHolder;

@SpringBootApplication
@EnableFeignClients
public class StoreApplication {

  public static void main(final String... args) {
    configureLocale();
    runApplication(args);
  }

  /**
   * Configures JVM and Spring to use defined local over OS one.
   */
  private static void configureLocale() {
    Locale.setDefault(Locale.US);
    LocaleContextHolder.setDefaultLocale(Locale.US);
  }

  private static void runApplication(final String[] args) {
    SpringApplication.run(StoreApplication.class, args);
  }
}
