package com.forfinance.dao;

import com.forfinance.domain.Order;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderDAOTest extends AbstractIntegrationTestCase {

    @Autowired
    private OrderDAO orderDAO;

    @Test
    public void findAll() {
        List<Order> orders = orderDAO.findAll();
        assertEquals(4, orders.size());
    }

    @Test
    public void findById() {
        Order order = orderDAO.findById(1L);
        assertNotNull(order);
        assertEquals(Long.valueOf("1"), order.getId());
    }

    @Test
    public void delete() {
        List<Order> orders = orderDAO.findAll();
        assertEquals(4, orders.size());

        orderDAO.delete(1L);
        orders = orderDAO.findAll();
        assertEquals(3, orders.size());
    }

    @Test
    public void getOrderCountForIpAddress() {
        Calendar startDate = GregorianCalendar.getInstance();
        startDate.set(2008, 9, 19, 23, 59, 59);
        Calendar endDate = GregorianCalendar.getInstance();
        endDate.set(2008, 9, 30, 0, 0, 0);

        Integer orderCount = orderDAO.getOrderCountForIpAddress(startDate, endDate, "222.222.222");
        assertEquals(2, orderCount.intValue());

        //
        startDate = GregorianCalendar.getInstance();
        startDate.set(2008, 9, 24, 0, 0, 0);
        orderCount = orderDAO.getOrderCountForIpAddress(startDate, endDate, "222.222.222");
        assertEquals(1, orderCount.intValue());
    }
}
