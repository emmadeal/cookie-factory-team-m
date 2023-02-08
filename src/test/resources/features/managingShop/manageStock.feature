Feature: Manage stock

  Background:
    Given an available recipe named "3 chocolates"
    And   a shop with 3 cookies
    And   the client with name "nicolas" start an order
    And   a client with name "thomas" start an order

  Scenario: add cookies to the the stock
    When we add 3 cookies to the shop's stock
    Then the shop has the recipe in stock
    Then the shop has 6 cookies in stock

  Scenario: first client choose one cookie
    When "nicolas" choose 1 cookie
    Then "nicolas" has 1 cookies in his basket
    Then the shop has 2 cookies in stock

  Scenario: first client choose all the cookies in stock
    When "nicolas" choose 3 cookie
    Then "nicolas" has 3 cookies in his basket
    Then the shop doesn't have this cookie anymore

  Scenario: second client choose one cookie but stock is empty
    Given "nicolas" choose 3 cookie
    When "thomas" choose 1 cookie
    Then "thomas" has 0 cookies in his basket
    Then the shop doesn't have this cookie anymore

  Scenario: the first client remove a cookie from his basket
    Given "nicolas" choose 3 cookie, then removes 1
    Then "nicolas" has 2 cookies in his basket
    Then the shop has 1 cookies in stock

  Scenario: the second client choose one cookie again
    Given "nicolas" choose 3 cookie, then removes 1
    When "thomas" choose 1 cookie
    Then "nicolas" has 2 cookies in his basket
    Then "thomas" has 1 cookies in his basket
    Then the shop doesn't have this cookie anymore