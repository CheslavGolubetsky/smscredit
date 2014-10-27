package com.forfinance.dao;

import com.forfinance.domain.Order;
import com.forfinance.exception.OrderNotFoundException;

import java.util.Calendar;
import java.util.List;

/**
 * Declares methods used to find order information.
 */
public interface OrderDAO {

    /**
     * Finds all orders.
     *
     * @return A list of order.
     */
    public List<Order> findAll();

    /**
     * Finds order by id.
     *
     * @param id - The id of the wanted order.
     * @return The found order. If no order is found, this method returns null.
     */
    public Order findById(Long id);

    /**
     * Creates a new order.
     *
     * @param source - The information of the created order.
     * @return The created order.
     */
    public Order create(Order source);

    /**
     * Updates the information of a order.
     *
     * @param source - The information of the updated order.
     * @return The updated order.
     * @throws OrderNotFoundException if no order is found with given id.
     */
    public Order update(Order source) throws OrderNotFoundException;

    /**
     * Deletes an order.
     *
     * @param orderId - The id of the deleted order.
     * @return The deleted order.
     * @throws OrderNotFoundException if no order is found with the given id.
     */
    public Order delete(Long orderId) throws OrderNotFoundException;

    /**
     * This method returns IP addresses count from Orders table.
     * Order's 'createTime' should be between 'startDate' and 'endDate'
     *
     * @param startDate - Calendar
     * @param endDate   - Calendar
     * @param ipAddress - String
     * @return Integer
     */
    int getOrderCountForIpAddress(Calendar startDate, Calendar endDate, String ipAddress);

}
