Feature: Submit a recipe
  Scenario:
    Given a chef who works in a factory with name "fred" who has 0 recipe to be validated
    When the chef submit a recipe named "superCookie"
    Then the factory has 1 recipe to be validated
    Then the name of the recipe to be validated is "superCookie"
