Feature: Monthly recipe

  Background:
    Given an available recipe with name "3 chocolates" buy 3 time
    And an available recipe2 with name "camarel" buy 1 time

  Scenario: monthly recipe
    When development of a new monthly recipe and deletion of the lowest selling recipe
    Then the new recipe is added to the list of the factory and the lowwest selling recipe is delete