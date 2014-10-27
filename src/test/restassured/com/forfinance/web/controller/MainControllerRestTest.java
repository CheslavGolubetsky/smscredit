package com.forfinance.web.controller;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class MainControllerRestTest {

    @Test
    public void getCustomers() throws Exception {
        given().
                when().
                get("http://localhost/sms-credit/customers").
                then().
                statusCode(200).
                body("id", hasItems(1, 2)).
                body("firstName", hasItems("Vasja", "Donki"));
    }

    @Test
    public void getCustomer() {
        given().
                when().
                get("http://localhost/sms-credit/customer/1").
                then().
                statusCode(200).
                body("id", equalTo(1)).
                body("firstName", equalTo("Vasja")).
                body("lastName", equalTo("Pupkin")).
                body("code", equalTo("123456789"));
    }

    @Test
    public void getCustomerHistory() {
        given().
                when().
                get("http://localhost/sms-credit/customer/1/history").
                then().
                statusCode(200).
                body("customer.id", equalTo(1)).
                body("orders.id", hasItems(1, 2));
    }

    @Test
    public void createCustomer() throws Exception {
        given().param("firstName", "Aaa").param("lastName", "Bbb").param("code", "qwerty").
                when().
                post("http://localhost/sms-credit/customer").
                then().
                statusCode(201).
                body("firstName", equalTo("Aaa")).
                body("lastName", equalTo("Bbb")).
                body("code", equalTo("qwerty"));
    }

    @Test
    public void createOrder() throws Exception {
        given().param("interest", "0.1").param("amount", "100").param("startDate", "20101010").param("endDate", "20101020").
                when().
                post("http://localhost/sms-credit/customer/1/order").
                then().
                statusCode(201).
                body("status", equalTo("SUCCESS")).
                body("message", equalTo("Order created successfully.")).
                body("response.amount", equalTo(100)).
                body("response.interest", equalTo(0.1f)).
                body("response.startDate", equalTo("20101010")).
                body("response.endDate", equalTo("20101020"));
    }

    @Test
    public void createExtension() throws Exception {
        given().param("interest", "0.1").param("amount", "100").param("startDate", "20071225").param("endDate", "20080101").
                when().
                post("http://localhost:8080/sms-credit/customer/1/order/1/extension").
                then().
                statusCode(201).
                body("status", equalTo("SUCCESS")).
                body("message", equalTo("Extension created successfully.")).
                body("response.amount", equalTo(100)).
                body("response.interest", equalTo(4.5f)).
                body("response.startDate", equalTo("20071225")).
                body("response.endDate", equalTo("20080101"));
    }
}
