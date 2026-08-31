Feature: Gym CRM and Trainer Workload integration

  As the Gym CRM system
  I want training changes to be delivered to the Trainer Workload service
  So that trainer monthly workload remains synchronized

  Scenario: Training creation updates trainer workload
    Given the Gym CRM and Trainer Workload services are available
    And a new trainee is registered for the integration test
    And the trainee is authenticated
    And an available training type is selected
    And a new trainer is registered for the integration test
    When a 60 minute training is created for the trainer
    Then the training should be created successfully
    And the trainer workload should eventually contain 60 minutes for that month

  Scenario: Invalid training does not update trainer workload
    Given the Gym CRM and Trainer Workload services are available
    And a new trainee is registered for the integration test
    And the trainee is authenticated
    And an available training type is selected
    And a new trainer is registered for the integration test
    When a training with duration 0 is submitted for the trainer
    Then the training request should be rejected
    And no trainer workload should be created for that month