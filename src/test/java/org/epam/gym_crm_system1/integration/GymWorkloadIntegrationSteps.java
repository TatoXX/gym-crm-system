package org.epam.gym_crm_system1.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GymWorkloadIntegrationSteps {

    private static final String GYM_BASE_URL =
            System.getenv().getOrDefault(
                    "GYM_BASE_URL",
                    "http://localhost:8080"
            );

    private static final String WORKLOAD_BASE_URL =
            System.getenv().getOrDefault(
                    "WORKLOAD_BASE_URL",
                    "http://localhost:8081"
            );

    private final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(
                            Duration.ofSeconds(5)
                    )
                    .build();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private String traineeUsername;
    private String traineePassword;

    private String trainerUsername;

    private String jwtToken;

    private Integer trainingTypeId;

    private LocalDate trainingDate;

    private int trainingResponseStatus;

    @Before
    public void resetScenario() {

        traineeUsername = null;
        traineePassword = null;
        trainerUsername = null;

        jwtToken = null;
        trainingTypeId = null;

        trainingDate = null;

        trainingResponseStatus = 0;
    }

    @Given(
            "the Gym CRM and Trainer Workload services are available"
    )
    public void servicesAreAvailable()
            throws Exception {

        HttpRequest gymRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        GYM_BASE_URL
                                                + "/actuator/health"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .GET()
                        .build();

        HttpResponse<String> gymResponse =
                send(gymRequest);

        assertTrue(
                gymResponse.statusCode() > 0,
                "Gym CRM service is not available"
        );


        HttpRequest workloadRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        WORKLOAD_BASE_URL
                                                + "/api/workloads/"
                                                + "availability-check"
                                                + "/years/2000/months/1"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .GET()
                        .build();

        HttpResponse<String> workloadResponse =
                send(workloadRequest);

        /*
         * Workload API is protected, so 401 without a token
         * actually proves that the HTTP service is responding.
         */
        assertEquals(
                401,
                workloadResponse.statusCode(),
                "Trainer Workload service did not respond as expected"
        );
    }

    @Given(
            "a new trainee is registered for the integration test"
    )
    public void registerTrainee()
            throws Exception {

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "firstName",
                "Integration"
        );

        body.put(
                "lastName",
                "Trainee"
        );

        body.put(
                "dateOfBirth",
                "2000-05-15"
        );

        body.put(
                "address",
                "Tbilisi"
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        GYM_BASE_URL
                                                + "/api/trainees"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        objectMapper.writeValueAsString(
                                                body
                                        )
                                )
                        )
                        .build();

        HttpResponse<String> response =
                send(request);

        assertEquals(
                200,
                response.statusCode(),
                "Trainee registration failed: "
                        + response.body()
        );

        JsonNode responseJson =
                objectMapper.readTree(
                        response.body()
                );

        traineeUsername =
                responseJson
                        .get("username")
                        .asText();

        traineePassword =
                responseJson
                        .get("password")
                        .asText();

        assertFalse(
                traineeUsername.isBlank()
        );

        assertFalse(
                traineePassword.isBlank()
        );
    }

    @Given("the trainee is authenticated")
    public void authenticateTrainee()
            throws Exception {

        String loginUrl =
                GYM_BASE_URL
                        + "/api/login"
                        + "?username="
                        + encode(traineeUsername)
                        + "&password="
                        + encode(traineePassword);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(loginUrl)
                        )
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                send(request);

        assertEquals(
                200,
                response.statusCode(),
                "Login failed: "
                        + response.body()
        );

        JsonNode responseJson =
                objectMapper.readTree(
                        response.body()
                );

        jwtToken =
                responseJson
                        .get("token")
                        .asText();

        assertFalse(
                jwtToken.isBlank()
        );
    }

    @Given("an available training type is selected")
    public void selectTrainingType()
            throws Exception {

        HttpRequest request =
                authenticatedRequest(
                        GYM_BASE_URL
                                + "/api/training-types"
                )
                        .GET()
                        .build();

        HttpResponse<String> response =
                send(request);

        assertEquals(
                200,
                response.statusCode(),
                "Could not obtain training types: "
                        + response.body()
        );

        JsonNode responseJson =
                objectMapper.readTree(
                        response.body()
                );

        assertTrue(
                responseJson.isArray(),
                "Training types response must be an array"
        );

        assertTrue(
                responseJson.size() > 0,
                "No training types exist in the Gym CRM database"
        );

        trainingTypeId =
                responseJson
                        .get(0)
                        .get("trainingTypeId")
                        .asInt();

        assertTrue(
                trainingTypeId > 0
        );
    }

    @Given(
            "a new trainer is registered for the integration test"
    )
    public void registerTrainer()
            throws Exception {

        assertNotNull(
                trainingTypeId,
                "Training type must be selected first"
        );

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "firstName",
                "Integration"
        );

        body.put(
                "lastName",
                "Trainer"
        );

        body.put(
                "specializationId",
                trainingTypeId
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        GYM_BASE_URL
                                                + "/api/trainers"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        objectMapper.writeValueAsString(
                                                body
                                        )
                                )
                        )
                        .build();

        HttpResponse<String> response =
                send(request);

        assertEquals(
                200,
                response.statusCode(),
                "Trainer registration failed: "
                        + response.body()
        );

        JsonNode responseJson =
                objectMapper.readTree(
                        response.body()
                );

        trainerUsername =
                responseJson
                        .get("username")
                        .asText();

        assertFalse(
                trainerUsername.isBlank()
        );
    }

    @When(
            "a {int} minute training is created for the trainer"
    )
    public void createValidTraining(
            int duration
    ) throws Exception {

        trainingResponseStatus =
                submitTraining(duration);
    }

    @When(
            "a training with duration {int} is submitted for the trainer"
    )
    public void createInvalidTraining(
            int duration
    ) throws Exception {

        trainingResponseStatus =
                submitTraining(duration);
    }

    @Then(
            "the training should be created successfully"
    )
    public void trainingShouldBeCreatedSuccessfully() {

        assertEquals(
                200,
                trainingResponseStatus
        );
    }

    @Then(
            "the training request should be rejected"
    )
    public void trainingRequestShouldBeRejected() {

        assertEquals(
                400,
                trainingResponseStatus
        );
    }

    @Then(
            "the trainer workload should eventually contain {int} minutes for that month"
    )
    public void workloadShouldEventuallyContain(
            int expectedDuration
    ) throws Exception {

        assertNotNull(trainingDate);
        assertNotNull(trainerUsername);

        long deadline =
                System.nanoTime()
                        + Duration
                        .ofSeconds(10)
                        .toNanos();

        HttpResponse<String> lastResponse =
                null;

        while (
                System.nanoTime()
                        < deadline
        ) {

            lastResponse =
                    requestCurrentWorkload();

            if (
                    lastResponse.statusCode()
                            == 200
            ) {

                int actualDuration =
                        Integer.parseInt(
                                lastResponse
                                        .body()
                                        .trim()
                        );

                if (
                        actualDuration
                                == expectedDuration
                ) {

                    return;
                }
            } else if (
                    lastResponse.statusCode()
                            != 404
            ) {

                fail(
                        "Unexpected workload response. "
                                + "Status: "
                                + lastResponse.statusCode()
                                + ", body: "
                                + lastResponse.body()
                );
            }

            Thread.sleep(250);
        }

        String lastResult =
                lastResponse == null
                        ? "No workload response received"
                        : "Status="
                        + lastResponse.statusCode()
                        + ", body="
                        + lastResponse.body();

        fail(
                "Trainer workload did not reach "
                        + expectedDuration
                        + " minutes within 10 seconds. "
                        + lastResult
        );
    }

    @Then(
            "no trainer workload should be created for that month"
    )
    public void noWorkloadShouldBeCreated()
            throws Exception {

        assertNotNull(trainingDate);
        assertNotNull(trainerUsername);

        long deadline =
                System.nanoTime()
                        + Duration
                        .ofSeconds(2)
                        .toNanos();

        while (
                System.nanoTime()
                        < deadline
        ) {

            HttpResponse<String> response =
                    requestCurrentWorkload();

            assertEquals(
                    404,
                    response.statusCode(),
                    "Invalid training unexpectedly changed workload. "
                            + "Response: "
                            + response.body()
            );

            Thread.sleep(250);
        }
    }

    private int submitTraining(
            int duration
    ) throws Exception {

        assertNotNull(traineeUsername);
        assertNotNull(trainerUsername);
        assertNotNull(jwtToken);

        trainingDate =
                LocalDate.now();

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "traineeUsername",
                traineeUsername
        );

        body.put(
                "trainerUsername",
                trainerUsername
        );

        body.put(
                "trainingName",
                "Integration Training"
        );

        body.put(
                "trainingDate",
                trainingDate.toString()
        );

        body.put(
                "trainingDuration",
                duration
        );

        HttpRequest request =
                authenticatedRequest(
                        GYM_BASE_URL
                                + "/api/trainings"
                )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        objectMapper.writeValueAsString(
                                                body
                                        )
                                )
                        )
                        .build();

        HttpResponse<String> response =
                send(request);

        return response.statusCode();
    }

    private HttpResponse<String> requestCurrentWorkload()
            throws Exception {

        String workloadUrl =
                WORKLOAD_BASE_URL
                        + "/api/workloads/"
                        + encode(trainerUsername)
                        + "/years/"
                        + trainingDate.getYear()
                        + "/months/"
                        + trainingDate.getMonthValue();

        HttpRequest request =
                authenticatedRequest(
                        workloadUrl
                )
                        .GET()
                        .build();

        return send(request);
    }

    private HttpRequest.Builder authenticatedRequest(
            String url
    ) {

        assertNotNull(
                jwtToken,
                "JWT token is required"
        );

        return HttpRequest.newBuilder()
                .uri(
                        URI.create(url)
                )
                .timeout(
                        Duration.ofSeconds(5)
                )
                .header(
                        "Authorization",
                        "Bearer " + jwtToken
                );
    }

    private HttpResponse<String> send(
            HttpRequest request
    ) throws Exception {

        return httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private String encode(
            String value
    ) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}