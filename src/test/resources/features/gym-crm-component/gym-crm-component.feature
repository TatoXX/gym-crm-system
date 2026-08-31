Feature: Gym CRM component behavior

  As a Gym CRM API client
  I want valid requests to be processed
  And invalid requests to be rejected

  Scenario: Successfully register a trainee
    Given a trainee registration request with first name "Cucumber", last name "Trainee", address "Tbilisi" and birth date "2000-05-15"
    When the trainee registration request is submitted
    Then the response status should be 200
    And the response username should be "Cucumber.Trainee"
    And the generated password should contain 10 characters

  Scenario: Reject trainee registration when first name is missing
    Given a trainee registration request with first name "", last name "Trainee", address "Tbilisi" and birth date "2000-05-15"
    When the trainee registration request is submitted
    Then the response status should be 400
    And the response message should contain "First name is required"

  Scenario: Reject unauthenticated access to a protected endpoint
    When I request the protected training types endpoint without authentication
    Then the response status should be 401