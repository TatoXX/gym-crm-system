package org.epam.gym_crm_system1.storage;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageInitializerTest {

    @Test
    void shouldInitializeStorageFromFiles() {
        Storage storage = new Storage();

        StorageInitializer initializer =
                new StorageInitializer(storage);

        ReflectionTestUtils.setField(
                initializer,
                "traineesFilePath",
                "src/main/resources/data/trainees.txt"
        );

        ReflectionTestUtils.setField(
                initializer,
                "trainersFilePath",
                "src/main/resources/data/trainers.txt"
        );

        ReflectionTestUtils.setField(
                initializer,
                "trainingsFilePath",
                "src/main/resources/data/trainings.txt"
        );

        initializer.initializeStorage();

        assertEquals(5, storage.getTrainees().size());
        assertEquals(5, storage.getTrainers().size());
        assertEquals(10, storage.getTrainings().size());
    }
}