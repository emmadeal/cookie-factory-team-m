Feature: Create a recipe

  Background:
    Given a catalog with different ingredients

  Scenario:
    When a chef create a recipe named "superCookie"
    Then the recipe has 1 dough
    Then the recipe has less than 2 flavors
    Then the recipe has less than 4 topping

  Scenario:
    When a chef create a recipe named "superCookie" and a recipe named "chocolateCookie"
    Then the two recipes are different