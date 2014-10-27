package com.forfinance.dozer;

import com.forfinance.domain.Customer;
import com.forfinance.domain.Order;
import com.forfinance.domain.OrderStatus;
import com.forfinance.domain.OrderType;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.OrderDTO;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class SpringBeanMappingBuilderTest {

    private DozerBeanMapper dozerBeanMapper;

    @Before
    public void setUp() {
        dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.addMapping(new SpringBeanMappingBuilder());
    }

    @Test
    public void testCustomer() {
        CustomerDTO createdDTO = new CustomerDTO();
        createdDTO.setId(11111111111L);
        createdDTO.setFirstName("Petr");
        createdDTO.setLastName("Petrov");
        createdDTO.setCode("12345678");

        Customer customer = dozerBeanMapper.map(createdDTO, Customer.class);
        assertNull(customer.getId());
        assertEquals("Petr", customer.getFirstName());
        assertEquals("Petrov", customer.getLastName());
        assertEquals("12345678", customer.getCode());
        assertTrue(customer.getOrderList().isEmpty());

        //
        Customer source = new Customer();
        source.setId(1L);
        source.setFirstName("Petr");
        source.setLastName("Petrov");
        source.setCode("12345678");

        CustomerDTO destination = dozerBeanMapper.map(source, CustomerDTO.class);
        assertEquals(Long.valueOf("1"), destination.getId());
        assertEquals("Petr", destination.getFirstName());
        assertEquals("Petrov", destination.getLastName());
        assertEquals("12345678", destination.getCode());

        //
        Customer testA = new Customer();
        source.setId(1L);
        Order testB = new Order();
        testB.setId(2L);
        testA.getOrderList().add(testB);

        CustomerDTO testC = new CustomerDTO();
        testC.setId(3L);
        dozerBeanMapper.map(testC, testA);
        assertEquals(1, testA.getOrderList().size());
        assertTrue(testA.getOrderList().contains(testB));
    }

    @Test
    public void testOrder() {
        Calendar createTime = GregorianCalendar.getInstance();
        Date startDate = new Date();
        Date endDate = new Date();

        OrderDTO createdDTO = new OrderDTO();
        createdDTO.setId(1L);
        createdDTO.setInterest(new BigDecimal(11.11));
        createdDTO.setAmount(new BigDecimal(22.22));
        createdDTO.setOrderType(OrderType.LOAN.name());
        createdDTO.setStartDate(startDate);
        createdDTO.setOrderStatus(OrderStatus.SUCCESS.name());
        createdDTO.setEndDate(endDate);

        Order order = dozerBeanMapper.map(createdDTO, Order.class);
        assertNull(order.getId());
        assertNull(order.getCreateTime());
        assertEquals(startDate, order.getStartDate());
        assertEquals(endDate, order.getEndDate());
        assertEquals(null, order.getOrderType());
        assertEquals(new BigDecimal(11.11), order.getInterest());
        assertEquals(new BigDecimal(22.22), order.getAmount());
        assertEquals(OrderStatus.SUCCESS, order.getOrderStatus());

        Order source = new Order();
        source.setId(1L);
        source.setInterest(new BigDecimal(11.11));
        source.setAmount(new BigDecimal(22.22));
        source.setOrderType(OrderType.LOAN);
        source.setOrderStatus(OrderStatus.SUCCESS);
        source.setCreateTime(createTime);
        source.setStartDate(startDate);
        source.setEndDate(endDate);

        Customer customer = new Customer();
        customer.setId(111L);
        source.setCustomer(customer);

        OrderDTO destination = dozerBeanMapper.map(source, OrderDTO.class);
        assertEquals(Long.valueOf("1"), destination.getId());
        assertEquals(startDate, destination.getStartDate());
        assertEquals(endDate, destination.getEndDate());
        assertEquals(OrderType.LOAN.name(), destination.getOrderType());
        assertEquals(OrderStatus.SUCCESS.name(), destination.getOrderStatus());
        assertEquals(new BigDecimal(11.11), destination.getInterest());
        assertEquals(new BigDecimal(22.22), destination.getAmount());
    }
}
