Feature: Validate a recipe

  Background:
    Given 1 dough1, 1 dough2, 1 flavor, 3 topping
    Given a recipe1 named "myCookie" with 1 dough1, 1 flavor, 0 topping to be validated

  Scenario: validating a recipe
    When a factoryManager validate the recipe1
    Then There is 1 recipe validated in the factory
    And  There is 0 recipe to be validated in the factory

  Scenario: no validating a recipe beacause of the name
    Given a recipe2 named "myCookie" with 1 dough2, 1 flavor, 0 topping belongs to the factory recipe list
    When a factoryManager validate the recipe1
    Then There is 0 recipe validated in the factory
    And  There is 0 recipe to be validated in the factory

  Scenario: no validating a recipe because of the ingredients
    Given a recipe2 named "caramel" with 1 dough1, 1 flavor, 0 topping belongs to the factory recipe list
    When a factoryManager validate the recipe1
    Then There is 0 recipe validated in the factory
    And  There is 0 recipe to be validated in the factory

  Scenario: no validating a recipe because of the price
    Given a recipe2 named "caramel" with 1 dough1, 1 flavor, 3 topping belongs to the factory recipe list
    When a factoryManager validate the recipe1
    Then There is 0 recipe validated in the factory
    And  There is 0 recipe to be validated in the factory