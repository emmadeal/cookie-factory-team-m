Feature: Shop add employees

  Background:
    Given a shop in "Grenoble"

  Scenario: add shop manager
    When the shop in "Grenoble" add a manager named "Emma"
    Then there is 1 manager in the shop in "Grenoble"

  Scenario: add cook
    When the shop in "Grenoble" add a cook named "khad"
    Then there is 1 cook in the shop in "Grenoble"

  Scenario: delete shop manager
    Given the shop in "Grenoble" add a manager named "Emma"
    When the shop in "Grenoble" delete a manager named "Emma"
    Then there is 0 manager in the shop in "Grenoble"

  Scenario: delete cook
    Given the shop in "Grenoble" add a cook named "khad"
    When the shop in "Grenoble" delete a cook named "khad"
    Then there is 0 cook in the shop in "Grenoble"

  #Scenario: reassign shop manager
    #When a shop in "Paris"
    #And the manager works for shop in "Grenoble"
    #And the shop in "Paris" add a manager
    #Then there is 0 manager in the shop in "Grenoble"
    #Then there is 1 manager in the shop in "Paris"

  #Scenario: reassign cook
    #When a shop in "Paris"
    #And the cook works for shop in "Grenoble"
    #And the shop in "Paris" add a cook
    #Then there is 0 cook in the shop in "Grenoble"
    #Then there is 1 cook in the shop in "Paris"