Feature: Client cancel an order

  Background:
    Given an available recipe  with name "3 chocolates"
    And a shop in city "Nice"  and tax 0.20 with a stock
    And a cook with name  "marie"


  Scenario: a client cancel his order
    When a client validate an order and cancel his order
    Then the order have state CANCEL and the cook have no this order in progress
    And the stock of the shop have always the number of ingredients

  Scenario: a client can't cancel his order
    When a client can't cancel his order because the order is not paid
    Then the order have state INPROGRESS and the cook have no this order in progress
    And the stock of the shop have lost ingredients


  Scenario: a client can't cancel his order
    When a client can't cancel his order because the order is preparing by the cook
    Then the order have state PAID and the cook have this order in progress
    And the stock of the shop have lost ingredients