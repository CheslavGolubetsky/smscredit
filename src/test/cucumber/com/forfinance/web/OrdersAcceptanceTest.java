package com.forfinance.web;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:orders.feature",
        format = "html:target/cucumber-html-report")
public class OrdersAcceptanceTest {

}
