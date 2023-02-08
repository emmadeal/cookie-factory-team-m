Feature: Add shop to Factory

  Scenario: add shop to factory
    When the factory add a shop in "Nice"
    Then there is 1 shop in the factory

  Scenario: don't add shop to factory
    When the factory don't add a shop
    Then there is 0 shop in the factory
