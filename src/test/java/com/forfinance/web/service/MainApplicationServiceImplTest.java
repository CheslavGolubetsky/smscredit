package com.forfinance.web.service;

import com.forfinance.domain.Customer;
import com.forfinance.domain.Order;
import com.forfinance.domain.OrderStatus;
import com.forfinance.domain.OrderType;
import com.forfinance.dto.ActionResponseDTO;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.OrderDTO;
import com.forfinance.service.PersistenceService;
import junit.framework.TestCase;
import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class MainApplicationServiceImplTest {

    @Mock
    @SuppressWarnings("unused")
    private DozerBeanMapper dozerBeanMapper;

    @Mock
    @SuppressWarnings("unused")
    private PersistenceService persistenceService;

    @InjectMocks
    @SuppressWarnings("unused")
    private MainApplicationServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCustomers() throws Exception {
        List<Customer> customerList = new ArrayList<>();
        Customer customer = new Customer();
        customerList.add(customer);
        Mockito.doReturn(customerList).when(persistenceService).getCustomers();

        CustomerDTO dto = new CustomerDTO();
        Mockito.doReturn(dto).when(dozerBeanMapper).map(customer, CustomerDTO.class);

        List<CustomerDTO> result = service.getCustomers();
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));

        Mockito.verify(persistenceService, VerificationModeFactory.times(1)).getCustomers();
        Mockito.verify(dozerBeanMapper, VerificationModeFactory.times(1)).map(customer, CustomerDTO.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetCustomer() throws Exception {
        CustomerDTO dozer = new CustomerDTO();
        Mockito.doReturn(dozer).when(dozerBeanMapper).map(Mockito.any(CustomerDTO.class), Mockito.any(Class.class));

        Mockito.doReturn(null).when(persistenceService).getCustomer(1L);
        CustomerDTO result = service.getCustomer(1L);
        assertNull(result);

        Mockito.verify(persistenceService, VerificationModeFactory.times(1)).getCustomer(1L);
        Mockito.verify(dozerBeanMapper, VerificationModeFactory.times(0)).map(Mockito.any(CustomerDTO.class), Mockito.any());

        //
        Customer customer = new Customer();
        Mockito.doReturn(customer).when(persistenceService).getCustomer(1L);
        result = service.getCustomer(1L);
        assertSame(dozer, result);
        Mockito.verify(dozerBeanMapper, VerificationModeFactory.times(1)).map(customer, CustomerDTO.class);
    }

    @Test
    public void testCreateCustomer() throws Exception {
        CustomerDTO dto = new CustomerDTO();

        Customer source = new Customer();
        Mockito.doReturn(source).when(dozerBeanMapper).map(dto, Customer.class);

        Customer created = new Customer();
        Mockito.doReturn(created).when(persistenceService).createCustomer(source);

        CustomerDTO destination = new CustomerDTO();
        Mockito.doReturn(destination).when(dozerBeanMapper).map(created, CustomerDTO.class);

        CustomerDTO result = service.createCustomer(dto);
        assertSame(destination, result);

        InOrder inOrder = Mockito.inOrder(dozerBeanMapper, persistenceService);
        inOrder.verify(dozerBeanMapper, VerificationModeFactory.times(1)).map(dto, Customer.class);
        inOrder.verify(persistenceService, VerificationModeFactory.times(1)).createCustomer(source);
        inOrder.verify(dozerBeanMapper, VerificationModeFactory.times(1)).map(created, CustomerDTO.class);
    }

    @Test
    public void createOrder() {
        MainApplicationServiceImpl serviceSpy = Mockito.spy(service);

        OrderDTO orderDTO = new OrderDTO();
        Long customerId = Long.valueOf("111");
        String customerIpAddress = "customerIpAddress";

        Mockito.doReturn(null).when(persistenceService).getCustomer(customerId);

        ActionResponseDTO result = serviceSpy.createOrder(orderDTO, customerId, customerIpAddress);
        Assert.assertEquals(OrderStatus.ERROR.name(), result.getStatus());
        Assert.assertEquals("Customer for id[" + customerId + "] doesn't found.", result.getMessage());
        Assert.assertNull(result.getResponse());
        Mockito.verify(dozerBeanMapper, VerificationModeFactory.times(0)).map(Mockito.any(OrderDTO.class), Mockito.any());

        //
        Mockito.reset(persistenceService);
        Customer customer = new Customer();
        Mockito.doReturn(customer).when(persistenceService).getCustomer(customerId);

        Order order = new Order();
        Mockito.doReturn(order).when(dozerBeanMapper).map(orderDTO, Order.class);

        OrderDTO postProcessed = new OrderDTO();
        Mockito.doReturn(postProcessed).when(dozerBeanMapper).map(order, OrderDTO.class);

        Mockito.doReturn(false).when(serviceSpy).isRiskTooHigh(order, customerIpAddress);

        result = serviceSpy.createOrder(orderDTO, customerId, customerIpAddress);
        Assert.assertEquals(OrderStatus.SUCCESS.name(), result.getStatus());
        Assert.assertEquals("Order created successfully.", result.getMessage());
        TestCase.assertSame(postProcessed, result.getResponse());

        List<Order> orderList = customer.getOrderList();
        Assert.assertEquals(1, orderList.size());

        Order createdOrder = orderList.get(0);
        assertNotNull(createdOrder.getCreateTime());
        Assert.assertEquals(OrderType.LOAN, createdOrder.getOrderType());
        Assert.assertEquals(OrderStatus.SUCCESS, createdOrder.getOrderStatus());
        Assert.assertEquals(customerIpAddress, createdOrder.getCustomerIpAddress());
        TestCase.assertSame(customer, createdOrder.getCustomer());

        InOrder inOrder = Mockito.inOrder(serviceSpy, dozerBeanMapper, persistenceService);
        inOrder.verify(persistenceService, VerificationModeFactory.times(1)).getCustomer(customerId);
        inOrder.verify(dozerBeanMapper, VerificationModeFactory.times(1)).map(orderDTO, Order.class);
        inOrder.verify(serviceSpy, VerificationModeFactory.times(1)).isRiskTooHigh(order, customerIpAddress);
        inOrder.verify(dozerBeanMapper, VerificationModeFactory.times(1)).map(order, OrderDTO.class);
        inOrder.verify(persistenceService, VerificationModeFactory.times(1)).updateCustomer(customer);
    }

    @Test
    public void isRiskTooHigh() {
        MainApplicationServiceImpl serviceSpy = Mockito.spy(service);

        Order order = new Order();
        String customerIpAddress = "customerIpAddress";

        Mockito.doReturn(false).when(serviceSpy).isMaxAmountReached(order);
        Mockito.doReturn(false).when(serviceSpy).isMaxApplicationsPerDayReached(customerIpAddress);

        assertFalse(serviceSpy.isRiskTooHigh(order, customerIpAddress));
        Mockito.verify(serviceSpy, VerificationModeFactory.times(1)).isMaxAmountReached(order);
        Mockito.verify(serviceSpy, VerificationModeFactory.times(1)).isMaxApplicationsPerDayReached(customerIpAddress);

        //
        Mockito.doReturn(true).when(serviceSpy).isMaxAmountReached(order);
        Mockito.doReturn(false).when(serviceSpy).isMaxApplicationsPerDayReached(customerIpAddress);
        Assert.assertTrue(serviceSpy.isRiskTooHigh(order, customerIpAddress));

        //
        Mockito.doReturn(false).when(serviceSpy).isMaxAmountReached(order);
        Mockito.doReturn(true).when(serviceSpy).isMaxApplicationsPerDayReached(customerIpAddress);
        Assert.assertTrue(serviceSpy.isRiskTooHigh(order, customerIpAddress));

        //
        Mockito.doReturn(true).when(serviceSpy).isMaxAmountReached(order);
        Mockito.doReturn(true).when(serviceSpy).isMaxApplicationsPerDayReached(customerIpAddress);
        Assert.assertTrue(serviceSpy.isRiskTooHigh(order, customerIpAddress));
    }

    @Test
    public void isMaxAmountReached() {
        Order order = new Order();
        order.setAmount(new BigDecimal("100"));

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        order.setCreateTime(calendar);

        Assert.assertFalse(service.isMaxAmountReached(order));

        //
        order.setAmount(new BigDecimal("1000"));
        Assert.assertTrue(service.isMaxAmountReached(order));

        //
        calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        order.setCreateTime(calendar);
        Assert.assertFalse(service.isMaxAmountReached(order));
    }

    @Test
    public void isMaxApplicationsPerDayReached() {
        String customerIpAddress = "customerIpAddress";
        Mockito.doReturn(2).when(persistenceService).getOrderCountForIpAddress(Mockito.any(Calendar.class), Mockito.any(Calendar.class), Mockito.eq("customerIpAddress"));
        Assert.assertFalse(service.isMaxApplicationsPerDayReached(customerIpAddress));
        Mockito.verify(persistenceService, VerificationModeFactory.times(1)).getOrderCountForIpAddress(Mockito.any(Calendar.class), Mockito.any(Calendar.class), Mockito.eq("customerIpAddress"));

        //
        Mockito.doReturn(3).when(persistenceService).getOrderCountForIpAddress(Mockito.any(Calendar.class), Mockito.any(Calendar.class), Mockito.eq("customerIpAddress"));
        Assert.assertTrue(service.isMaxApplicationsPerDayReached(customerIpAddress));
    }
}
