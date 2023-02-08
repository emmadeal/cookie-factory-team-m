Feature: Client order a Recipe

  Background:
    Given an available recipe with name "3 chocolates"
    And   a shop in city "Nice" and tax 0.20 with a stock
    And  a cook with name "marie"

  Scenario: a client begin an order :  choose shop
    When the client choose shop
    Then the actual order of client have a shop


  Scenario: a client add recipe in this basket
    When a client add 4 recipe in this basket
    Then the client have 4 recipe in this basket
    And the stock have lost ingredients of this 4 recipe


  Scenario: a client delete recipe in this basket
    When a client add 4 recipe and after delete 1 recipe in this basket
    Then the client have 3 recipe in this basket
    And the stock have lost ingredients of this 3 recipe

  Scenario: a client choose a pickup hour
    When a client choose a pickup hour
    Then the actual order of client have a pickup hour and cook

  Scenario: a client validate the order
    When after choose shop , choose recipe and pickup hour the client validate his order
    Then the order is in repository , the is PAID and the actual order of client is empty
    And the order is in progress for a cook


  Scenario: a client pick up his order
    When after validate the order the order his ready,the client pick up the order
    Then the order have state TAKEN



