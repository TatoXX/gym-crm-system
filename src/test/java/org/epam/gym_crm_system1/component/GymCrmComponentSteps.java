package org.epam.gym_crm_system1.component;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class GymCrmComponentSteps {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private Map<String, Object> traineeRegistrationRequest;
    private MvcResult mvcResult;

    public GymCrmComponentSteps(
            MockMvc mockMvc,
            ObjectMapper objectMapper
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Given(
            "a trainee registration request with first name {string}, " +
                    "last name {string}, address {string} and birth date {string}"
    )
    public void aTraineeRegistrationRequest(
            String firstName,
            String lastName,
            String address,
            String birthDate
    ) {

        traineeRegistrationRequest = new LinkedHashMap<>();

        traineeRegistrationRequest.put(
                "firstName",
                firstName
        );

        traineeRegistrationRequest.put(
                "lastName",
                lastName
        );

        traineeRegistrationRequest.put(
                "address",
                address
        );

        traineeRegistrationRequest.put(
                "dateOfBirth",
                birthDate
        );
    }

    @When("the trainee registration request is submitted")
    public void theTraineeRegistrationRequestIsSubmitted()
            throws Exception {

        mvcResult =
                mockMvc.perform(
                                post("/api/trainees")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        traineeRegistrationRequest
                                                )
                                        )
                        )
                        .andReturn();
    }

    @When(
            "I request the protected training types endpoint without authentication"
    )
    public void requestProtectedEndpointWithoutAuthentication()
            throws Exception {

        mvcResult =
                mockMvc.perform(
                                get("/api/training-types")
                        )
                        .andReturn();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(
            int expectedStatus
    ) {

        assertEquals(
                expectedStatus,
                mvcResult.getResponse().getStatus()
        );
    }

    @Then("the response username should be {string}")
    public void theResponseUsernameShouldBe(
            String expectedUsername
    ) throws Exception {

        JsonNode response =
                objectMapper.readTree(
                        mvcResult
                                .getResponse()
                                .getContentAsString()
                );

        assertEquals(
                expectedUsername,
                response.get("username").asText()
        );
    }

    @Then(
            "the generated password should contain {int} characters"
    )
    public void theGeneratedPasswordShouldContainCharacters(
            int expectedLength
    ) throws Exception {

        JsonNode response =
                objectMapper.readTree(
                        mvcResult
                                .getResponse()
                                .getContentAsString()
                );

        String password =
                response.get("password").asText();

        assertEquals(
                expectedLength,
                password.length()
        );
    }

    @Then("the response message should contain {string}")
    public void theResponseMessageShouldContain(
            String expectedMessage
    ) throws Exception {

        JsonNode response =
                objectMapper.readTree(
                        mvcResult
                                .getResponse()
                                .getContentAsString()
                );

        String message =
                response.get("message").asText();

        assertTrue(
                message.contains(expectedMessage),
                "Expected response message to contain: "
                        + expectedMessage
                        + ", but was: "
                        + message
        );
    }
}