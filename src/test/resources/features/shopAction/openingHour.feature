Feature: Add opening hour

  Background:
    Given the available recipe with name "Vanille" and preparing time 10 minutes
    And the shop with city "Nice" and openingHour 8h and closingHour 18h
    And a client

  Scenario: The client choose a good pickHour
    When the client is starting an order with a shop and 2 "Vanille" recipes in the basket
    When the client choose 17h30 as a pickup hour for his order
    Then the client can order

  Scenario: The client choose a pickHour after the closing hour
    When the client is starting an order with a shop and 2 "Vanille" recipes in the basket
    When the client choose 18h30 as a pickup hour for his order
    Then the client cant order

  Scenario: The client choose a pickHour before the opening hour
    When the client is starting an order with a shop and 2 "Vanille" recipes in the basket
    When the client choose 7h30 as a pickup hour for his order
    Then the client cant order

  Scenario: The client choose a good pickHour but long prep time is before opening hour
    When the client is starting an order with a shop and 2 "Vanille" recipes in the basket
    When the client choose 8h10 as a pickup hour for his order
    Then the client cant order