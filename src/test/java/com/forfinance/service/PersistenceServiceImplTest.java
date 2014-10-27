package com.forfinance.service;

import com.forfinance.dao.CustomerDAO;
import com.forfinance.domain.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;


@RunWith(MockitoJUnitRunner.class)
public class PersistenceServiceImplTest {

    @Mock
    @SuppressWarnings("unused")
    private CustomerDAO customerDAO;

    @InjectMocks
    @SuppressWarnings("unused")
    private PersistenceServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCustomers() throws Exception {
        List<Customer> customers = new ArrayList<>();
        Mockito.doReturn(customers).when(customerDAO).findAll();

        List<Customer> result = service.getCustomers();
        assertSame(customers, result);
        Mockito.verify(customerDAO, VerificationModeFactory.times(1)).findAll();
    }

    @Test
    public void testGetCustomer() throws Exception {
        Customer customer = new Customer();
        Mockito.doReturn(customer).when(customerDAO).findById(1L);

        Customer result = service.getCustomer(1L);
        assertSame(result, customer);
        Mockito.verify(customerDAO, VerificationModeFactory.times(1)).findById(1L);
    }

    @Test
    public void testCreateCustomer() throws Exception {
        Customer source = new Customer();
        Customer destination = new Customer();
        Mockito.doReturn(destination).when(customerDAO).create(source);

        Customer result = service.createCustomer(source);
        assertSame(result, destination);
        Mockito.verify(customerDAO, VerificationModeFactory.times(1)).create(source);
    }
}
