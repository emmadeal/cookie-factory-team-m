Feature: user order a Recipe

  Background:
    Given an  available recipe with  name "3 chocolates"
    And a  shop  in city "Nice" and tax 0.20 with a stock
    And a  cook with name "marie"
    And user with name "claire" and password "1234" signin and login

  Scenario: the user is notify
    When  after 5 minute the user is notify
    Then the user have the notifications


  Scenario: the user is notify
    When  after 1h  the user is notify
    Then the user have the notifications

  Scenario: the order is obsolete
    When  after 2h  the order begin obsolete
    Then the order is obsolete



