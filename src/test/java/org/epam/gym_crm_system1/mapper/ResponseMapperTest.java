package org.epam.gym_crm_system1.mapper;

import org.epam.gym_crm_system1.dto.response.*;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ResponseMapperTest {

    private final ResponseMapper responseMapper = new ResponseMapper();

    @Test
    void toTrainingTypeResponse_WhenTrainingTypeIsNull_ShouldReturnNull() {
        TrainingTypeResponse response = responseMapper.toTrainingTypeResponse(null);

        assertNull(response);
    }

    @Test
    void toTrainingTypeResponse_ShouldMapTrainingType() {
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        when(trainingType.getId()).thenReturn(1);
        when(trainingType.getName()).thenReturn("Fitness");

        TrainingTypeResponse response = responseMapper.toTrainingTypeResponse(trainingType);

        assertEquals(1, response.getTrainingTypeId());
        assertEquals("Fitness", response.getTrainingTypeName());
    }

    @Test
    void toTrainerSummaryResponse_ShouldMapTrainer() {
        Trainer trainer = Mockito.mock(Trainer.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        when(trainer.getUserName()).thenReturn("Trainer.One");
        when(trainer.getFirstName()).thenReturn("Trainer");
        when(trainer.getLastName()).thenReturn("One");
        when(trainer.getTrainingType()).thenReturn(trainingType);

        when(trainingType.getId()).thenReturn(1);
        when(trainingType.getName()).thenReturn("Fitness");

        TrainerSummaryResponse response = responseMapper.toTrainerSummaryResponse(trainer);

        assertEquals("Trainer.One", response.getUsername());
        assertEquals("Trainer", response.getFirstName());
        assertEquals("One", response.getLastName());
        assertEquals(1, response.getSpecialization().getTrainingTypeId());
        assertEquals("Fitness", response.getSpecialization().getTrainingTypeName());
    }

    @Test
    void toTraineeSummaryResponse_ShouldMapTrainee() {
        Trainee trainee = Mockito.mock(Trainee.class);

        when(trainee.getUserName()).thenReturn("John.Smith");
        when(trainee.getFirstName()).thenReturn("John");
        when(trainee.getLastName()).thenReturn("Smith");

        TraineeSummaryResponse response = responseMapper.toTraineeSummaryResponse(trainee);

        assertEquals("John.Smith", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
    }

    @Test
    void toTrainerSummaryResponseList_WhenInputIsNull_ShouldReturnEmptyList() {
        List<TrainerSummaryResponse> response = responseMapper.toTrainerSummaryResponseList(null);

        assertTrue(response.isEmpty());
    }

    @Test
    void toTraineeSummaryResponseList_WhenInputIsNull_ShouldReturnEmptyList() {
        List<TraineeSummaryResponse> response = responseMapper.toTraineeSummaryResponseList(null);

        assertTrue(response.isEmpty());
    }

    @Test
    void toTraineeProfileResponse_ShouldMapTraineeProfile() {
        Trainee trainee = Mockito.mock(Trainee.class);
        Trainer trainer = Mockito.mock(Trainer.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        when(trainee.getFirstName()).thenReturn("John");
        when(trainee.getLastName()).thenReturn("Smith");
        when(trainee.getDateOfBirth()).thenReturn(LocalDate.of(2000, 5, 10));
        when(trainee.getAddress()).thenReturn("Tbilisi");
        when(trainee.getIsActive()).thenReturn(true);
        when(trainee.getTrainers()).thenReturn(Set.of(trainer));

        when(trainer.getUserName()).thenReturn("Trainer.One");
        when(trainer.getFirstName()).thenReturn("Trainer");
        when(trainer.getLastName()).thenReturn("One");
        when(trainer.getTrainingType()).thenReturn(trainingType);

        when(trainingType.getId()).thenReturn(1);
        when(trainingType.getName()).thenReturn("Fitness");

        TraineeProfileResponse response = responseMapper.toTraineeProfileResponse(trainee);

        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals(LocalDate.of(2000, 5, 10), response.getDateOfBirth());
        assertEquals("Tbilisi", response.getAddress());
        assertTrue(response.getIsActive());
        assertEquals(1, response.getTrainers().size());
        assertEquals("Trainer.One", response.getTrainers().get(0).getUsername());
    }

    @Test
    void toUpdateTraineeProfileResponse_ShouldMapUpdatedTraineeProfile() {
        Trainee trainee = Mockito.mock(Trainee.class);

        when(trainee.getUserName()).thenReturn("John.Smith");
        when(trainee.getFirstName()).thenReturn("John");
        when(trainee.getLastName()).thenReturn("Smith");
        when(trainee.getDateOfBirth()).thenReturn(LocalDate.of(2000, 5, 10));
        when(trainee.getAddress()).thenReturn("Tbilisi");
        when(trainee.getIsActive()).thenReturn(true);
        when(trainee.getTrainers()).thenReturn(Set.of());

        UpdateTraineeProfileResponse response = responseMapper.toUpdateTraineeProfileResponse(trainee);

        assertEquals("John.Smith", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals(LocalDate.of(2000, 5, 10), response.getDateOfBirth());
        assertEquals("Tbilisi", response.getAddress());
        assertTrue(response.getIsActive());
        assertTrue(response.getTrainers().isEmpty());
    }

    @Test
    void toTrainerProfileResponse_ShouldMapTrainerProfile() {
        Trainer trainer = Mockito.mock(Trainer.class);
        Trainee trainee = Mockito.mock(Trainee.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        when(trainer.getFirstName()).thenReturn("Jane");
        when(trainer.getLastName()).thenReturn("Smith");
        when(trainer.getTrainingType()).thenReturn(trainingType);
        when(trainer.getIsActive()).thenReturn(true);
        when(trainer.getTrainees()).thenReturn(Set.of(trainee));

        when(trainingType.getId()).thenReturn(1);
        when(trainingType.getName()).thenReturn("Fitness");

        when(trainee.getUserName()).thenReturn("John.Smith");
        when(trainee.getFirstName()).thenReturn("John");
        when(trainee.getLastName()).thenReturn("Smith");

        TrainerProfileResponse response = responseMapper.toTrainerProfileResponse(trainer);

        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals(1, response.getSpecialization().getTrainingTypeId());
        assertEquals("Fitness", response.getSpecialization().getTrainingTypeName());
        assertTrue(response.getIsActive());
        assertEquals(1, response.getTrainees().size());
        assertEquals("John.Smith", response.getTrainees().get(0).getUsername());
    }

    @Test
    void toUpdateTrainerProfileResponse_ShouldMapUpdatedTrainerProfile() {
        Trainer trainer = Mockito.mock(Trainer.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        when(trainer.getUserName()).thenReturn("Jane.Smith");
        when(trainer.getFirstName()).thenReturn("Jane");
        when(trainer.getLastName()).thenReturn("Smith");
        when(trainer.getTrainingType()).thenReturn(trainingType);
        when(trainer.getIsActive()).thenReturn(true);
        when(trainer.getTrainees()).thenReturn(Set.of());

        when(trainingType.getId()).thenReturn(1);
        when(trainingType.getName()).thenReturn("Fitness");

        UpdateTrainerProfileResponse response = responseMapper.toUpdateTrainerProfileResponse(trainer);

        assertEquals("Jane.Smith", response.getUsername());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals(1, response.getSpecialization().getTrainingTypeId());
        assertEquals("Fitness", response.getSpecialization().getTrainingTypeName());
        assertTrue(response.getIsActive());
        assertTrue(response.getTrainees().isEmpty());
    }
}