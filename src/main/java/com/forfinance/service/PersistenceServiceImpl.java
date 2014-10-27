package com.forfinance.service;

import com.forfinance.dao.CustomerDAO;
import com.forfinance.dao.OrderDAO;
import com.forfinance.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class PersistenceServiceImpl implements PersistenceService {

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public List<Customer> getCustomers() {
        return customerDAO.findAll();
    }

    @Override
    public Customer getCustomer(Long customerId) {
        return customerDAO.findById(customerId);
    }

    @Override
    public Customer createCustomer(Customer source) {
        return customerDAO.create(source);
    }

    @Override
    public Customer updateCustomer(Customer source) {
        return customerDAO.update(source);
    }

    @Override
    public int getOrderCountForIpAddress(Calendar startDate, Calendar endDate, String ipAddress) {
        return orderDAO.getOrderCountForIpAddress(startDate, endDate, ipAddress);
    }
}
