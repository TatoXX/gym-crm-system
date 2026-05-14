package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {

    @Test
    void shouldSaveTrainee() {

        Storage storage = new Storage();

        TraineeDao traineeDao =
                new TraineeDao(storage);

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000,1,1),
                        1
                );

        traineeDao.saveTrainee(trainee);

        assertEquals(trainee,
                traineeDao.findTraineeById(1));
    }

    @Test
    void shouldUpdateTrainee() {

        Storage storage = new Storage();

        TraineeDao traineeDao =
                new TraineeDao(storage);

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000,1,1),
                        1
                );

        traineeDao.saveTrainee(trainee);

        trainee.setAddress("Batumi");

        traineeDao.updateTrainee(trainee);

        assertEquals("Batumi",
                traineeDao.findTraineeById(1).getAddress());
    }

    @Test
    void shouldDeleteTrainee() {

        Storage storage = new Storage();

        TraineeDao traineeDao =
                new TraineeDao(storage);

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000,1,1),
                        1
                );

        traineeDao.saveTrainee(trainee);

        traineeDao.deleteTraineeById(1);

        assertNull(traineeDao.findTraineeById(1));
    }
}