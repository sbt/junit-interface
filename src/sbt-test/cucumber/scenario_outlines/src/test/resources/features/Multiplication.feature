@my-tag
Feature: Multiplication
  In order to avoid making mistakes
  As a dummy
  I want to multiply numbers

  Scenario Outline: Multiply two variables
    Given a variable x with value <x>
    And a variable y with value <y>
    When I multiply x * y
    Then I get <z>
    Examples:
    | x | y | z |
    | 1 | 1 | 1 |
    | 2 | 3 | 1 |

