package com.forfinance.web.service;

import com.forfinance.dto.ActionResponseDTO;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.HistoryDTO;
import com.forfinance.dto.OrderDTO;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MainApplicationService {

    List<CustomerDTO> getCustomers();

    CustomerDTO getCustomer(Long customerId);

    CustomerDTO createCustomer(CustomerDTO dto);

    HistoryDTO getCustomerHistory(Long customerId);

    ActionResponseDTO createOrder(OrderDTO orderDTO, Long customerId, String customerIpAddress);

    ActionResponseDTO createExtension(OrderDTO extensionDTO, Long customerId, Long orderId, String customerIpAddress);

    String getClientIpAddress(HttpServletRequest request);

    ActionResponseDTO createInvalidAttributeResponse(BindingResult result);

}
