Feature: Assign an order to a Cook

  Background:
    Given an available recipe with name "Vanille", cooking time 11 minutes and preparing time 9 minutes
    And a shop of name "Nice"
    And a factory manager with name "Morgane"
    And a cook with name "Emma" and a cook with name "Jules"
    And a client with name "Khad"

  Scenario: The shop assign the order to the only available cook (Sprint 2)
    When the client add 1 recipe
    And  the cook "Emma" has an order to deliver at 10h00
    And  the cook "Jules" has an order to deliver at 14h30
    When the client choose 14h30 as a pickup hour
    Then the shop manager assign the order to the cook "Emma"

  Scenario: The shop assign the order to one of the available cooks (Sprint 2)
    When the client add 1 recipe
    And  the cook "Emma" has an order to deliver at 10h00
    And  the cook "Jules" has an order to deliver at 8h00
    When the client choose 14h30 as a pickup hour
    Then the shop manager assign the order to one of the available cooks

  Scenario: The shop doesn't have an available cook for the hour (Sprint 2)
    When the client add 1 recipe
    And  the cook "Emma" has an order to deliver at 14h30
    And  the cook "Jules" has an order to deliver at 14h30
    When the client choose 14h30 as a pickup hour
    Then the shop manager does not assign a cook

  Scenario: The shop assign the order to the only available cook (Sprint 3)
    When the client add 1 recipe
    And  the cook "Emma" has an order to deliver at 10h00 with 20 minutes of preparation
    And  the cook "Jules" has an order to deliver at 14h20 with 20 minutes of preparation
    When the client choose 14h30 as a pickup hour
    Then the shop manager assign the order to the cook "Emma"

  Scenario: The shop assign the order to one of the available cooks (Sprint 3)
    When the client add 1 recipe
    And  the cook "Emma" has an order to deliver at 10h00 with 20 minutes of preparation
    And  the cook "Jules" has an order to deliver at 8h00 with 20 minutes of preparation
    When the client choose 14h30 as a pickup hour
    Then the shop manager assign the order to one of the available cooks

  Scenario: The shop doesn't have an available cook for the hour (Sprint 3)
    When the client add 1 recipe
    And  the cook "Emma" has an order to deliver at 14h30 with 20 minutes of preparation
    And  the cook "Jules" has an order to deliver at 14h10 with 20 minutes of preparation
    When the client choose 14h30 as a pickup hour
    Then the shop manager does not assign a cook

  Scenario: The shop assign the order to the only available cook (Sprint 4)
    When  the cook "Emma" has an order to deliver at 10h00 with 20 minutes of preparation
    And  the cook "Jules" doesnt know how to do the "Cat" theme
    And  the cook "Emma" know how to do the "Cat" theme
    And  the cook "Jules" has an order to deliver at 8h00 with 20 minutes of preparation
    And the client add 1 party recipe
    And  the client choose "Cat" as a theme
    When the client choose 14h30 as a pickup hour
    Then the shop manager assign the order to the cook "Emma"

  Scenario: The shop doesn't have an available cook for the hour (Sprint 4)
    When  the cook "Emma" has an order to deliver at 14h30 with 20 minutes of preparation
    And  the cook "Emma" doesnt know how to do the "Cat" theme
    And  the cook "Jules" has an order to deliver at 14h10 with 20 minutes of preparation
    And  the cook "Jules" doesnt know how to do the "Cat" theme
    And the client add 1 party recipe
    And  the client choose "Cat" as a theme
    When the client choose 14h30 as a pickup hour
    Then the shop manager does not assign a cook