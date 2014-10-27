Feature: Customer
  
  Scenario: Check customers
    When the client requests customers from DB
    Then client see the following customers:
    | firstName | lastName | code      |
    | Vasja     | Pupkin   | 123456789 |
    | Donki     | Hot      | 987654321 |

  Scenario: Check customer
    When the client requests customer with ID=1
    Then the client see user "Vasja" "Pupkin"

  Scenario: Check customer history
    When the client requests history for customer with ID=1
    Then the client see history user "Vasja" "Pupkin"
    And following history orders:
    | startDate | endDate  | orderStatus | orderType | interest | amount  |
    | 20071220  | 20071225 | SUCCESS     | LOAN      | 3.00     | 200.00  |
    | 20071225  | 20071230 | ERROR       | LOAN      | 3.00     | 1000.00 |

