package pl.itrack.airqeye.store;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class HealthCheckITest {

    private static final String ACTUATOR_URL = "http://localhost:%s/actuator/info";

    @Value("${local.management.port}")
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void managementEndpointIsResponsive() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(String.format(ACTUATOR_URL, this.port), Map.class);

        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
