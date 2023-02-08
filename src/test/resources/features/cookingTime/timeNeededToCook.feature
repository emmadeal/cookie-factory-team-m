Feature: How many time a cook need to prepare the order

  Background:
    Given an available recipe named "Vanille" and with cooking time 3 minutes and preparing time 9 minutes
    And client starting an order

  Scenario: The order takes less than 15 minutes
    When the client add 1 recipes to his basket and order for 14h00
    Then the order take 12 minutes to prepare
    Then the cook start to cook at 13h45

  Scenario: The order takes less than 30 minutes but more than 15
    When the client add 2 recipes to his basket and order for 14h00
    Then the order take 21 minutes to prepare
    Then the cook start to cook at 13h30

  Scenario: The order takes more than 1 hour
    When the client add 7 recipes to his basket and order for 14h00
    Then the order take 66 minutes to prepare
    Then the cook start to cook at 12h45
