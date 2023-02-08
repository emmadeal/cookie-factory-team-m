Feature: user order a Recipe

  Background:
    Given an  available recipe with name "3 chocolates"
    And a shop  in city "Nice" and tax 0.20 with a stock
    And a cook  with name "marie"
    And user with name "claire" and password "1234" signin

  Scenario: a user begin an order :  choose shop
    When the user with name "claire" and password "1234" choose shop
    Then the actual order of user have a shop


  Scenario: a user add recipe in this basket
    When a user with name "claire" and password "1234" add 4 recipe in this basket
    Then the user have 4 recipe in this basket
    And the stock  have lost ingredients of this 4 recipe


  Scenario: a user delete recipe in this basket
    When a user with name "claire" and password "1234" add 4 recipe and after delete 1 recipe in this basket
    Then the user have 3 recipe in this basket
    And the stock  have lost ingredients of this 3 recipe

  Scenario: a user choose a pickup hour
    When a user with name "claire" and password "1234" choose a pickup hour
    Then the actual order of user have a pickup hour and cook

  Scenario: a user validate the order
    When after choose shop , choose recipe and pickup hour the user with name "claire" and password "1234" validate his order
    Then the order is in repository , the is PAID and the actual order of user is empty
    And the order  is in progress for a cook


  Scenario: a user pick up his order
    When after validate the order the order his ready,the user with name "claire" and password "1234" pick up the order
    Then the order  have state TAKEN


  Scenario: a user can't begin an order :  choose shop
    When a user with name "claire" and password "1234" cancel 2 order in 8 minutes
    Then the user can't order

