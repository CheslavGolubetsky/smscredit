Feature: Orders and Extensions

  Scenario: Create order
    When the client create order for customer with ID=1 and start date "20101010", end date "20101020", interest "0.1", amount "100.00"
    Then the client see status "SUCCESS" and message "Order created successfully."

  Scenario: Create extension
    When the client create extension for customer with ID=2 and order with ID=3 and start date "20081025", end date "20081101", interest "0.1", amount "20.00"
    Then the client see status "SUCCESS" and message "Extension created successfully."

