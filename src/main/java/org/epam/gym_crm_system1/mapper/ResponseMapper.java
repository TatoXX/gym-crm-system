package org.epam.gym_crm_system1.mapper;

import org.epam.gym_crm_system1.dto.response.*;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.TrainingType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseMapper {

    public TrainingTypeResponse toTrainingTypeResponse(TrainingType trainingType) {
        if (trainingType == null) {
            return null;
        }

        return new TrainingTypeResponse(
                trainingType.getId(),
                trainingType.getName()
        );
    }

    public TrainerSummaryResponse toTrainerSummaryResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        return new TrainerSummaryResponse(
                trainer.getUserName(),
                trainer.getFirstName(),
                trainer.getLastName(),
                toTrainingTypeResponse(trainer.getTrainingType())
        );
    }

    public TraineeSummaryResponse toTraineeSummaryResponse(Trainee trainee) {
        if (trainee == null) {
            return null;
        }

        return new TraineeSummaryResponse(
                trainee.getUserName(),
                trainee.getFirstName(),
                trainee.getLastName()
        );
    }

    public List<TrainerSummaryResponse> toTrainerSummaryResponseList(Iterable<Trainer> trainers) {
        List<TrainerSummaryResponse> responses = new ArrayList<>();

        if (trainers == null) {
            return responses;
        }

        for (Trainer trainer : trainers) {
            responses.add(toTrainerSummaryResponse(trainer));
        }

        return responses;
    }

    public List<TraineeSummaryResponse> toTraineeSummaryResponseList(Iterable<Trainee> trainees) {
        List<TraineeSummaryResponse> responses = new ArrayList<>();

        if (trainees == null) {
            return responses;
        }

        for (Trainee trainee : trainees) {
            responses.add(toTraineeSummaryResponse(trainee));
        }

        return responses;
    }

    public TraineeProfileResponse toTraineeProfileResponse(Trainee trainee) {
        if (trainee == null) {
            return null;
        }

        return new TraineeProfileResponse(
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getIsActive(),
                toTrainerSummaryResponseList(trainee.getTrainers())
        );
    }

    public UpdateTraineeProfileResponse toUpdateTraineeProfileResponse(Trainee trainee) {
        if (trainee == null) {
            return null;
        }

        return new UpdateTraineeProfileResponse(
                trainee.getUserName(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getIsActive(),
                toTrainerSummaryResponseList(trainee.getTrainers())
        );
    }

    public TrainerProfileResponse toTrainerProfileResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        return new TrainerProfileResponse(
                trainer.getFirstName(),
                trainer.getLastName(),
                toTrainingTypeResponse(trainer.getTrainingType()),
                trainer.getIsActive(),
                toTraineeSummaryResponseList(trainer.getTrainees())
        );
    }

    public UpdateTrainerProfileResponse toUpdateTrainerProfileResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        return new UpdateTrainerProfileResponse(
                trainer.getUserName(),
                trainer.getFirstName(),
                trainer.getLastName(),
                toTrainingTypeResponse(trainer.getTrainingType()),
                trainer.getIsActive(),
                toTraineeSummaryResponseList(trainer.getTrainees())
        );
    }
}