Feature: user join loyalty program

  Background:
    Given an available recipe in a shop with a cook

  Scenario: a user join the loyalty program
    Given a client with name Claire
    Given the client "Claire" sign in  with the  password "motdepasse" with the mail "claire.lpb@gmail.com" with the phone "0654321321"
    Given the client with name "Claire" and password "motdepasse" login
    When user with name Claire join the loyalty program
    Then the user is membership

  Scenario: the user don't have reduction on his order
    Given the client "Claire" sign in  with the  password "motdepasse" with the mail "claire.lpb@gmail.com" with the phone "0654321321"
    Given the client with name "Claire" and password "motdepasse" login
    Given user with name Claire begin, pays and take an order with 28 cookies
    When user with name Claire begin and pays an order with 2 cookies
    Then the user don't have reduction on his order

  Scenario: the user have reduction on his order
    Given the client "Claire" sign in  with the  password "motdepasse" with the mail "claire.lpb@gmail.com" with the phone "0654321321"
    Given the client with name "Claire" and password "motdepasse" login
    Given user with name Claire begin, pays and take an order with 30 cookies
    When user with name Claire begin and pays an order with 2 cookies
    Then the user have reduction on his order


