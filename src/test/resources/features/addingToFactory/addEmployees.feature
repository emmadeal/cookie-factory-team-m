Feature: add employees to Factory

  Scenario: add factory manager to factory
    When the factory add a factory manager named "Claire"
    Then there is 1 factory manager working for the factory

  Scenario: add chef to factory
    When the factory add a chef named "Morgane"
    Then there is 1 chef working for the factory

  Scenario: delete factory manager from factory
    When the factory fire the factory manager named "Claire"
    Then there is 0 factory manager working for the factory

  Scenario: delete chef from factory manager
    When the factory fire the chef named "Morgane"
    Then there is 0 chef working for the factory