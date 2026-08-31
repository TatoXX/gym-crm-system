Feature: Trainer workload component behavior

  As the trainer workload service
  I want workload messages to be validated and processed
  So that trainer monthly workload remains correct

  Scenario: Successfully process a valid trainer workload message
    Given a valid ADD workload message for trainer "Cucumber.Trainer" with duration 60 on "2026-08-15"
    When the workload message is consumed
    Then the workload message should be processed successfully
    And trainer "Cucumber.Trainer" should have 60 minutes for year 2026 and month 8

  Scenario: Reject workload message with invalid training duration
    Given an invalid workload message for trainer "Cucumber.Trainer" with duration 0 on "2026-08-15"
    When the workload message is consumed
    Then the workload message should be rejected as invalid
    And no trainer workload should be saved