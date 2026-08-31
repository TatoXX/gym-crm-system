package org.epam.trainerworkloadservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.epam.trainerworkloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.crypto.SecretKey;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "jwt.secret=0123456789012345678901234567890123456789",
                "eureka.client.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false",
                "spring.cloud.discovery.enabled=false",
                "spring.data.mongodb.auto-index-creation=false"
        }
)
class WorkloadSecurityIntegrationTest {

    private static final String SECRET =
            "0123456789012345678901234567890123456789";

    @LocalServerPort
    private int port;

    @MockitoBean
    private TrainerWorkloadService trainerWorkloadService;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {

        httpClient = HttpClient.newHttpClient();

        when(
                trainerWorkloadService.getMonthlyWorkload(
                        "Security.Unknown.Trainer",
                        2026,
                        8
                )
        ).thenReturn(null);
    }

    @Test
    void shouldReturnUnauthorizedWhenBearerTokenIsMissing()
            throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(getWorkloadUrl()))
                        .GET()
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(
                401,
                response.statusCode()
        );
    }

    @Test
    void shouldReturnUnauthorizedWhenBearerTokenIsInvalid()
            throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(getWorkloadUrl()))
                        .header(
                                "Authorization",
                                "Bearer invalid-token"
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(
                401,
                response.statusCode()
        );
    }

    @Test
    void shouldAllowRequestWhenBearerTokenIsValid()
            throws Exception {

        String token = createValidToken();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(getWorkloadUrl()))
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        /*
         * 404 is expected because the mocked workload
         * service has no workload for this trainer.
         *
         * The important point is that security allowed
         * the authenticated request through instead of
         * returning 401.
         */
        assertEquals(
                404,
                response.statusCode()
        );
    }

    private String getWorkloadUrl() {

        return "http://localhost:"
                + port
                + "/api/workloads/"
                + "Security.Unknown.Trainer"
                + "/years/2026/months/8";
    }

    private String createValidToken() {

        SecretKey key =
                Keys.hmacShaKeyFor(
                        SECRET.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return Jwts.builder()
                .subject("Security.Test.User")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 60_000
                        )
                )
                .signWith(key)
                .compact();
    }
}