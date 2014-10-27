package com.forfinance.dao;

import com.forfinance.domain.Customer;
import com.forfinance.exception.CustomerNotFoundException;

import java.util.List;

/**
 * Declares methods used to obtain and modify customer information.
 */
public interface CustomerDAO {

    /**
     * Creates a new customer.
     *
     * @param source - The information of the created customer.
     * @return The created customer.
     */
    public Customer create(Customer source);

    /**
     * Deletes a customer.
     *
     * @param customerId - The id of the deleted customer.
     * @return The deleted customer.
     * @throws CustomerNotFoundException if no customer is found with the given id.
     */
    public Customer delete(Long customerId) throws CustomerNotFoundException;

    /**
     * Finds all customers.
     *
     * @return A list of customers.
     */
    public List<Customer> findAll();

    /**
     * Finds customer by id.
     *
     * @param id - The id of the wanted customer.
     * @return The found customer. If no customer is found, this method returns null.
     */
    public Customer findById(Long id);

    /**
     * Updates the information of a customer.
     *
     * @param source - The information of the updated customer.
     * @return The updated customer.
     * @throws CustomerNotFoundException if no customer is found with given id.
     */
    public Customer update(Customer source) throws CustomerNotFoundException;

}
