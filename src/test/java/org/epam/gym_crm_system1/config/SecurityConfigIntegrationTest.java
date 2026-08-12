package org.epam.gym_crm_system1.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectedEndpointWithoutToken_ShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(
                        get("/api/training-types")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void traineeRegistrationWithoutAuthentication_ShouldBePublic()
            throws Exception {

        /*
         * Invalid body intentionally gives 400.
         *
         * The important point is that Security does NOT
         * stop the request with 401.
         */
        mockMvc.perform(
                        post("/api/trainees")
                                .contentType(
                                        "application/json"
                                )
                                .content(
                                        "{}"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void trainerRegistrationWithoutAuthentication_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                        post("/api/trainers")
                                .contentType(
                                        "application/json"
                                )
                                .content(
                                        "{}"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void loginWithoutAuthentication_ShouldBePublic()
            throws Exception {

        /*
         * Missing required username/password gives 400,
         * proving the request reached MVC validation
         * instead of being rejected by Security with 401.
         */
        mockMvc.perform(
                        get("/api/login")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void basicAuthentication_ShouldNotAuthenticateProtectedEndpoint()
            throws Exception {

        String credentials =
                "John.Smith:password123";

        String encoded =
                Base64.getEncoder()
                        .encodeToString(
                                credentials.getBytes(
                                        StandardCharsets.UTF_8
                                )
                        );

        mockMvc.perform(
                        get("/api/training-types")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Basic " + encoded
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void corsGetPreflight_ShouldBeAllowed()
            throws Exception {

        mockMvc.perform(
                        options(
                                "/api/training-types"
                        )
                                .header(
                                        HttpHeaders.ORIGIN,
                                        "http://localhost:3000"
                                )
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                        "GET"
                                )
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                        "Authorization"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        header().exists(
                                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
                        )
                )
                .andExpect(
                        header().string(
                                HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                                containsString("GET")
                        )
                );
    }

    @Test
    void corsPatchPreflight_ShouldBeAllowed()
            throws Exception {

        mockMvc.perform(
                        options(
                                "/api/trainees/status"
                        )
                                .header(
                                        HttpHeaders.ORIGIN,
                                        "http://localhost:3000"
                                )
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                        "PATCH"
                                )
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                        "Authorization, Content-Type"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        header().exists(
                                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
                        )
                )
                .andExpect(
                        header().string(
                                HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                                containsString("PATCH")
                        )
                );
    }
}