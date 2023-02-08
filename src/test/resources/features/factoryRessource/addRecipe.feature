Feature: Add a recipe

  Scenario: recipe validated
    Given a new recipe with name "3 chocolates"
    Then There is 0 available recipe in the factory
    And There is 0 validated recipe in the factory
    And  There is 1 recipe to be validated by the factoryManager
    And the factory manager validate it
    And There is 1 validated recipe in the factory
    And the factory manager add it
    And There is 1 available recipe in the factory
    And  There is 0 recipe to be validated by the factoryManager