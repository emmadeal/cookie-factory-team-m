Feature: Cooks prepare an order

  Background:
    Given a shop in city "Marseille" with a cook named "Charles"
    And an available recipe
    And a client validate an order

  Scenario: the cook starts cooking the order
    When the cook starts cooking order
    Then the cook has 1 order in progress in his list

  Scenario: the cook finished cooking the order
    When the cook finished cooking order
    Then there is 1 order ready in the list
    Then the cook has 0 order in progress in his list