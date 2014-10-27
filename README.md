========= REST requests =========
/customers                                          -- GET  -- returns all customers as JSON
/customer/{customerId}                              -- GET  -- returns customer by ID
/customer                                           -- POST -- creates a new customer and returns created customer as JSON
    parameters: "firstName"     - mandatory
                "lastName"      - mandatory
                "code"          - mandatory
/customer/{customerId}/history                      -- GET  -- returns customer history (customer data and orders) by customer ID
/customer/{customerId}/order                        -- POST -- creates a new order and returns created order as JSON
    parameters: "startDate"     - mandatory - format "yyyyMMdd"
                "endDate"       - mandatory - format "yyyyMMdd"
                "interest"      - mandatory
                "amount"        - mandatory
/customer/{customerId}/order/{orderId}/extension    -- POST -- creates a extension for customer/order and returns created extension as JSON


========= How to run application =========
mvn jetty:run


========= If you want to submit a POST request =========
1. You have to set the “request header” section of the Firefox plugin
   to have a “name” = “Content-Type” and “value” = “application/x-www-form-urlencoded”
2. Now, you are able to submit parameter like “name=mynamehere&title=TA”
   in the “request body” text area field


========= Example 1 =========
http://localhost:8080/sms-credit/customers

Status Code: 200 OK
Content-Type: application/json;charset=UTF-8
[{"id":1,"firstName":"Vasja","lastName":"Pupkin","code":"123456789"},{"id":2,"firstName":"Donki","lastName":"Hot","code":"987654321"}]


========= Example 2 =========
http://localhost:8080/sms-credit/customer/1

Status Code: 200 OK
Content-Type: application/json;charset=UTF-8
{"id":1,"firstName":"Vasja","lastName":"Pupkin","code":"123456789"}


========= Example 3 =========
http://localhost:8080/sms-credit/customer
firstName=A&lastName=B&code=C

Status Code: 201 Created
Content-Type: application/json;charset=UTF-8
{"id":3,"firstName":"A","lastName":"B","code":"C"}


========= Example 4 =========
http://localhost:8080/sms-credit/customer/2/history

Status Code: 200 OK
Content-Type: application/json;charset=UTF-8
{"customer":{"id":2,"firstName":"Donki","lastName":"Hot","code":"987654321"},"orders":[{"id":3,"orderStatus":"success","orderType":"loan","startDate":"20081020","endDate":"20081025","interest":3.00,"amount":200.00},{"id":4,"orderStatus":"success","orderType":"extension","startDate":"20081025","endDate":"20081030","interest":4.50,"amount":300.00}]}


========= Example 5 =========
http://localhost:8080/sms-credit/customer/1/order
startDate=20101010&endDate=20101020&interest=0.1&amount=100

Status Code: 200 OK or 201 Created
Content-Type: application/json;charset=UTF-8

{"message":"Order rejected due to high risk.","status":"error"}
or
{"message":"Order created successfully.","status":"success","response":{"orderStatus":"success","orderType":"loan","startDate":"20101010","endDate":"20101020","interest":0.1,"amount":100}}


========= Example 6 =========
http://localhost:8080/sms-credit/customer/1/order/1/extension
startDate=20071225&endDate=20080101&interest=0.1&amount=100

Status Code: 200 OK or 201 Created
Content-Type: application/json;charset=UTF-8

{"message":"Some mandatory attributes are missing or invalid","status":"error","response":["startDate"]}
or
{"message":"Extension created successfully.","status":"success","response":{"orderStatus":"success","orderType":"extension","startDate":"20101010","endDate":"20101017","interest":4.50,"amount":100}}
