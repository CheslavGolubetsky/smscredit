package com.forfinance.repository;

import com.forfinance.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByIpAndDate(String customerIpAddress, Calendar startDate, Calendar endDate);

}
