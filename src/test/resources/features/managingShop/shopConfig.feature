Feature: Config shop assets

  Background:
    Given a shop with a shop manager

  Scenario: set closing time
    When the shopManager set closing hour to 19:25
    Then the shop has closing hour set to 19:25

  Scenario: set opening time
    When the shopManager set opening hour to 9:45
    Then the shop has opening hour set to 9:45

  Scenario: set tax
    When the shopManager set tax to 20.0%
    Then the shop tax is at 20.0%