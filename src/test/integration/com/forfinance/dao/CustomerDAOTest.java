package com.forfinance.dao;

import com.forfinance.domain.Customer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class CustomerDAOTest extends AbstractIntegrationTestCase {

    @Autowired
    private CustomerDAO customerDAO;

    @Test
    public void testBaseMethods() {
        // test 'findAll'
        List<Customer> customers = customerDAO.findAll();
        assertEquals(2, customers.size());

        Customer source = new Customer();
        source.setFirstName("Petr");
        source.setLastName("Petrov");
        source.setCode("12345678");

        // test 'createCustomer'
        Customer created = customerDAO.create(source);
        Long createdId = created.getId();
        assertNotNull(createdId);

        customers = customerDAO.findAll();
        assertEquals(3, customers.size());

        boolean found = false;
        for (Customer customer : customers) {
            if ("Petr".equals(customer.getFirstName()) &&
                    "Petrov".equals(customer.getLastName()) &&
                    "12345678".equals(customer.getCode())) {
                found = true;
            }
        }
        if (!found) {
            fail("Customer 'Petr' not found");
        }

        // test 'update'
        Customer toUpdate = new Customer();
        toUpdate.setId(createdId);
        toUpdate.setFirstName("Ivan");
        toUpdate.setLastName("Ivanov");
        toUpdate.setCode("987654321");
        customerDAO.update(toUpdate);

        // test 'findById'
        Customer updated = customerDAO.findById(createdId);
        assertNotNull(updated);
        assertEquals("Ivan", updated.getFirstName());
        assertEquals("Ivanov", updated.getLastName());
        assertEquals("987654321", updated.getCode());

        // test 'delete'
        customerDAO.delete(createdId);
        customers = customerDAO.findAll();
        assertEquals(2, customers.size());

        found = false;
        for (Customer customer : customers) {
            if ("Petr".equals(customer.getFirstName()) || "Ivan".equals(customer.getFirstName())) {
                found = true;
            }
        }
        if (found) {
            fail("Created customer wasn't deleted");
        }
    }
}
