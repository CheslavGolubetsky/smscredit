package com.forfinance.web;

import com.forfinance.dto.ActionResponseDTO;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrdersStepDefinitions {
    private Client client = ClientBuilder.newClient();

    private ActionResponseDTO responseDTO;

    @When("^the client create order for customer with ID=(\\d+) and start date \"([^\"]*)\", end date \"([^\"]*)\", interest \"([^\"]*)\", amount \"([^\"]*)\"$")
    public void clientCreateOrder(int customerId, String startDate, String endDate, String interest, String amount) {
        String deliveryUrl = "http://localhost:8080/sms-credit/customer/" + customerId + "/order";
        WebTarget target = client.target(deliveryUrl);

        Form form = new Form();
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("interest", interest);
        form.param("amount", amount);
        responseDTO = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), ActionResponseDTO.class);
    }

    @Then("^the client see status \"([^\"]*)\" and message \"([^\"]*)\"$")
    public void clientCheckCreationResponse(String status, String message) {
        assertNotNull(responseDTO);
        assertEquals(responseDTO.getStatus(), status);
        assertEquals(responseDTO.getMessage(), message);
    }

    @When("^the client create extension for customer with ID=(\\d+) and order with ID=(\\d+) and start date \"([^\"]*)\", end date \"([^\"]*)\", interest \"([^\"]*)\", amount \"([^\"]*)\"$")
    public void clientCreateExtension(int customerId, int orderId, String startDate, String endDate, String interest, String amount) {
        String deliveryUrl = "http://localhost:8080/sms-credit/customer/" + customerId + "/order/" + orderId + "/extension";
        WebTarget target = client.target(deliveryUrl);

        Form form = new Form();
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("interest", interest);
        form.param("amount", amount);
        responseDTO = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), ActionResponseDTO.class);
    }
}
