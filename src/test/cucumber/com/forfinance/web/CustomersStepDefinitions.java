package com.forfinance.web;

import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.HistoryDTO;
import com.forfinance.dto.OrderDTO;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

public class CustomersStepDefinitions {
    private Client client = ClientBuilder.newClient();

    private List<CustomerDTO> dbCustomers;
    private CustomerDTO dbCustomer;
    private HistoryDTO dbHistory;

    @When("^the client requests customers from DB$")
    public void clientRequestsCustomersFromDataBase() throws Throwable {
        GenericType<List<CustomerDTO>> list = new GenericType<List<CustomerDTO>>() {
        };
        String deliveryUrl = "http://localhost:8080/sms-credit/customers";
        WebTarget target = client.target(deliveryUrl);
        dbCustomers = target.request(MediaType.APPLICATION_JSON).get(list);
    }

    @Then("^client see the following customers:$")
    public void clientCheckFollowingCustomers(List<CustomerDTO> expectedCustomers) {
        assertNotNull(dbCustomers);
        assertFalse(dbCustomers.isEmpty());

        for (CustomerDTO expectedCustomer : expectedCustomers) {
            assertTrue(dbCustomers.contains(expectedCustomer));
        }
    }

    @When("^the client requests customer with ID=(\\d+)$")
    public void clientRequestsCustomer(int customerId) {
        String deliveryUrl = "http://localhost:8080/sms-credit/customer/" + customerId;
        WebTarget target = client.target(deliveryUrl);
        dbCustomer = target.request(MediaType.APPLICATION_JSON).get(CustomerDTO.class);
    }

    @Then("^the client see user \"([^\"]*)\" \"([^\"]*)\"$")
    public void clientCheckCustomer(String firstName, String lastName) {
        assertNotNull(dbCustomer);
        assertEquals(dbCustomer.getFirstName(), firstName);
        assertEquals(dbCustomer.getLastName(), lastName);
    }

    @When("^the client requests history for customer with ID=(\\d+)$")
    public void clientRequestsHistory(int customerId) {
        String deliveryUrl = "http://localhost:8080/sms-credit/customer/" + customerId + "/history";
        WebTarget target = client.target(deliveryUrl);
        dbHistory = target.request(MediaType.APPLICATION_JSON).get(HistoryDTO.class);
    }

    @Then("^the client see history user \"([^\"]*)\" \"([^\"]*)\"$")
    public void clientCheckHistoryCustomer(String firstName, String lastName) {
        assertNotNull(dbHistory);
        assertNotNull(dbHistory.getCustomer());
        assertEquals(dbHistory.getCustomer().getFirstName(), firstName);
        assertEquals(dbHistory.getCustomer().getLastName(), lastName);
    }

    @And("^following history orders:$")
    public void clientCheckHistoryOrders(List<Map<String, String>> orders) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setDateFormatSymbols(DateFormatSymbols.getInstance(Locale.ENGLISH));

        List<OrderDTO> dbHistoryOrders = dbHistory.getOrders();
        assertFalse(dbHistoryOrders.isEmpty());

        for (Map<String, String> orderDetails : orders) {
            OrderDTO checkedDTO = new OrderDTO();
            checkedDTO.setOrderStatus(orderDetails.get("orderStatus"));
            checkedDTO.setOrderType(orderDetails.get("orderType"));
            checkedDTO.setInterest(new BigDecimal(orderDetails.get("interest")));
            checkedDTO.setAmount(new BigDecimal(orderDetails.get("amount")));
            checkedDTO.setStartDate(dateFormat.parse(orderDetails.get("startDate")));
            checkedDTO.setEndDate(dateFormat.parse(orderDetails.get("endDate")));
            assertTrue(dbHistoryOrders.contains(checkedDTO));
        }
    }
}
