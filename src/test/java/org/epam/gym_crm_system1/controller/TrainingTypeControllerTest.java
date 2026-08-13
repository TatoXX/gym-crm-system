package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.security.JwtAuthenticationFilter;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrainingTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getTrainingTypes_ShouldReturnTrainingTypesList() throws Exception {

        TrainingType fitness = new TrainingType();

        fitness.setId(1);
        fitness.setName("Fitness");

        TrainingType yoga = new TrainingType();

        yoga.setId(2);
        yoga.setName("Yoga");

        when(
                trainingTypeService.findAllTrainingTypes()
        ).thenReturn(
                List.of(
                        fitness,
                        yoga
                )
        );

        mockMvc.perform(
                        get("/api/training-types")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[0].trainingTypeId"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$[0].trainingTypeName"
                        ).value("Fitness")
                )
                .andExpect(
                        jsonPath(
                                "$[1].trainingTypeId"
                        ).value(2)
                )
                .andExpect(
                        jsonPath(
                                "$[1].trainingTypeName"
                        ).value("Yoga")
                );
    }
}