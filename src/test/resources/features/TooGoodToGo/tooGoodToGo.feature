Feature: simuler une connexion à TooGoodToGo

  Background:
    Given a recipe with name "superCookie" belongs to the factory recipe list
    And a shop with city "Nice" has 5 cookies in stock
    And a shopManager
    And a cook with named "Nicolas"
    And a client who pays for 4 cookies
    And TooGoodToGo has no surprise baskets in his list
    And a TooGoodToGo client with no surprise basket reserved

  ##Scenario: Le shop manager crée un panier surprise
    ##Given 2 hours later, the client hasn't take is order
    ##When 6 hours have passed
    ##Then there is 1 surprise basket in the shop's surprise baskets list
    ##And there is 2 surprise basket in the TooGoodToGo surprise baskets list

  Scenario: Un client réserve un panier déja réservé
    Given a surprise basket reserved in the TooGoodToGo surprise baskets list
    When a client try to order the surprise basket
    Then he has 0 surprise basket

  Scenario: Un client reserve un panier disponible
    Given a surprise basket available in the TooGoodToGo surprise baskets list
    When a client try to order the surprise basket
    Then he has 1 surprise basket

  Scenario: Le client va récupérer son panier surprise
    Given the shop have 1 surprise basket added to TooGoodToGo
    And a TooGoodToGo client ordered the surprise basket
    When the client takes his order
    Then there is 0 surprise basket in the shop's surprise baskets list
    Then there is 0 surprise basket in the TooGoodToGo surprise baskets list

  Scenario: A new surprise basket is available
    When a new surprise basket is created
    Then the user recieve a new notification