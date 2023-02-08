Feature: user authentication

  Background:
    Given a user signing in with username "khad", password "motdepasse", phone "0607080910" and mail "khad@unice.com"

  Scenario: user sign in
    Then the user with username "khad" and password "motdepasse" exists

  Scenario: user log in
    Given a user login with username "khad" and password "motdepasse"
    Then the user corresponds to a signed user