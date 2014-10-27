package com.forfinance.dao;

import com.forfinance.domain.Customer;
import com.forfinance.exception.CustomerNotFoundException;
import com.forfinance.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerDAOImpl implements CustomerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDAOImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    @Override
    public Customer create(Customer source) {
        LOGGER.debug("Creating a new customer: " + source);
        return customerRepository.save(source);
    }

    @Transactional(rollbackFor = CustomerNotFoundException.class)
    @Override
    public Customer delete(Long customerId) throws CustomerNotFoundException {
        Customer deleted = customerRepository.findOne(customerId);
        if (deleted == null) {
            LOGGER.debug("No customer found with id [" + customerId + "] to delete.");
            throw new CustomerNotFoundException();
        }

        LOGGER.debug("Deleting customer with id: " + customerId);
        customerRepository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Customer> findAll() {
        LOGGER.debug("Finding all customers");
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Customer findById(Long id) {
        LOGGER.debug("Finding customer by id: " + id);
        return customerRepository.findOne(id);
    }

    @Transactional(rollbackFor = CustomerNotFoundException.class)
    @Override
    public Customer update(Customer updated) throws CustomerNotFoundException {
        Customer customer = customerRepository.findOne(updated.getId());
        if (customer == null) {
            LOGGER.debug("No customer found with id [" + updated.getId() + "] to update.");
            throw new CustomerNotFoundException();
        }

        LOGGER.debug("Updating customer: " + updated);
        return customerRepository.save(updated);
    }
}
