package com.forfinance.dao;


import com.forfinance.domain.Order;
import com.forfinance.exception.OrderNotFoundException;
import com.forfinance.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
class OrderDAOImpl implements OrderDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAOImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Order> findAll() {
        LOGGER.debug("Finding all orders");
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Order findById(Long id) {
        LOGGER.debug("Finding order by id: " + id);
        return orderRepository.findOne(id);
    }

    @Transactional
    @Override
    public Order create(Order source) {
        LOGGER.debug("Creating a new order: " + source);
        return orderRepository.save(source);
    }

    @Transactional(rollbackFor = OrderNotFoundException.class)
    @Override
    public Order update(Order source) throws OrderNotFoundException {
        Order order = orderRepository.findOne(source.getId());
        if (order == null) {
            LOGGER.debug("No order found with id [" + source.getId() + "] to update.");
            throw new OrderNotFoundException();
        }

        LOGGER.debug("Updating order: " + source);
        return orderRepository.save(source);
    }

    @Transactional(rollbackFor = OrderNotFoundException.class)
    @Override
    public Order delete(Long orderId) throws OrderNotFoundException {
        Order deleted = orderRepository.findOne(orderId);
        if (deleted == null) {
            LOGGER.debug("No order found with id [" + orderId + "] to delete.");
            throw new OrderNotFoundException();
        }

        LOGGER.debug("Deleting order with id: " + orderId);
        orderRepository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public int getOrderCountForIpAddress(Calendar startDate, Calendar endDate, String ipAddress) {
        return orderRepository.findByIpAndDate(ipAddress, startDate, endDate).size();
    }
}
