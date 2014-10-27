package com.forfinance.service;

import com.forfinance.domain.Customer;

import java.util.Calendar;
import java.util.List;

public interface PersistenceService {

    List<Customer> getCustomers();

    Customer getCustomer(Long customerId);

    Customer createCustomer(Customer source);

    Customer updateCustomer(Customer source);

    int getOrderCountForIpAddress(Calendar startDate, Calendar endDate, String ipAddress);

}
