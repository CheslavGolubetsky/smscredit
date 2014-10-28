package com.forfinance.web.service;

import com.forfinance.domain.Customer;
import com.forfinance.domain.Order;
import com.forfinance.domain.OrderStatus;
import com.forfinance.domain.OrderType;
import com.forfinance.dto.ActionResponseDTO;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.HistoryDTO;
import com.forfinance.dto.OrderDTO;
import com.forfinance.service.PersistenceService;
import org.dozer.DozerBeanMapper;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
class MainApplicationServiceImpl implements MainApplicationService {
    public static final BigDecimal MAX_POSSIBLE_AMOUNT = new BigDecimal("500.00");

    @Autowired
    private PersistenceService persistenceService;
    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    @Override
    public List<CustomerDTO> getCustomers() {
        List<Customer> customerList = persistenceService.getCustomers();

        List<CustomerDTO> destinationDtoList = new ArrayList<>();
        for (Customer customer : customerList) {
            CustomerDTO dto = dozerBeanMapper.map(customer, CustomerDTO.class);
            destinationDtoList.add(dto);
        }

        return destinationDtoList;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        Customer customer = persistenceService.getCustomer(customerId);
        if (customer != null) {
            return dozerBeanMapper.map(customer, CustomerDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer source = dozerBeanMapper.map(dto, Customer.class);
        Customer created = persistenceService.createCustomer(source);
        return dozerBeanMapper.map(created, CustomerDTO.class);
    }

    @Override
    public HistoryDTO getCustomerHistory(Long customerId) {
        Customer customer = persistenceService.getCustomer(customerId);
        if (customer != null) {
            CustomerDTO customerDTO = dozerBeanMapper.map(customer, CustomerDTO.class);
            HistoryDTO result = new HistoryDTO();
            result.setCustomer(customerDTO);

            List<Order> orderList = customer.getOrderList();
            for (Order order : orderList) {
                OrderDTO orderDTO = dozerBeanMapper.map(order, OrderDTO.class);
                result.getOrders().add(orderDTO);
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method returns an ActionResponseDTO which contains operation status and message
     * and for success status a newly created object.
     * But some extension cases aren't specified:
     * - interest amount and interest validation?
     *
     * @param orderDTO          - OrderDTO
     * @param customerId        - Long
     * @param customerIpAddress - String
     * @return ActionResponseDTO
     */
    @Override
    public ActionResponseDTO createOrder(OrderDTO orderDTO, Long customerId, String customerIpAddress) {
        Customer customer = persistenceService.getCustomer(customerId);
        if (customer == null) {
            return createActionResponse(OrderStatus.ERROR.name(), "Customer for id[" + customerId + "] doesn't found.");
        }

        Order order = dozerBeanMapper.map(orderDTO, Order.class);
        order.setCustomer(customer);
        order.setCreateTime(GregorianCalendar.getInstance());
        order.setOrderType(OrderType.LOAN);
        order.setCustomerIpAddress(customerIpAddress);
        customer.getOrderList().add(order);

        boolean riskTooHigh = isRiskTooHigh(order, customerIpAddress);
        ActionResponseDTO response;
        if (riskTooHigh) {
            order.setOrderStatus(OrderStatus.ERROR);
            response = createActionResponse(OrderStatus.ERROR.name(), "Order rejected due to high risk.");
        } else {
            order.setOrderStatus(OrderStatus.SUCCESS);
            response = createActionResponse(OrderStatus.SUCCESS.name(), "Order created successfully.");
            OrderDTO result = dozerBeanMapper.map(order, OrderDTO.class);
            response.setResponse(result);
        }

        persistenceService.updateCustomer(customer);
        return response;
    }

    protected boolean isRiskTooHigh(Order order, String customerIpAddress) {
        boolean maxAmountReached = isMaxAmountReached(order);
        boolean maxApplicationsReached = isMaxApplicationsPerDayReached(customerIpAddress);
        return maxAmountReached || maxApplicationsReached;
    }

    /**
     * This method checks that max.amount is reached in time from 00:00 till 08:00 (24H)
     *
     * @param order Order
     * @return boolean
     */
    protected boolean isMaxAmountReached(Order order) {
        if (MAX_POSSIBLE_AMOUNT.compareTo(order.getAmount()) > 0) {
            return false;
        }

        int hoursIn24Format = order.getCreateTime().get(Calendar.HOUR_OF_DAY);
        return hoursIn24Format < 8;
    }

    /**
     * This method checks that 3 or more time an order was created during a day from one IP address.
     *
     * @param customerIpAddress String
     * @return boolean
     */
    protected boolean isMaxApplicationsPerDayReached(String customerIpAddress) {
        Calendar startDate = GregorianCalendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -1);
        int orderCountForIpAddress = persistenceService.getOrderCountForIpAddress(startDate, GregorianCalendar.getInstance(), customerIpAddress);
        return orderCountForIpAddress > 2;
    }

    /**
     * This method returns an ActionResponseDTO which contains operation status and message
     * and for success status a newly created object.
     * But some extension cases aren't specified:
     * - if current loan has an extension what we should do?
     * - interest amount and interest validation?
     *
     * @param extensionDTO      - OrderDTO
     * @param customerId        - Long
     * @param orderId           - Long
     * @param customerIpAddress - String
     * @return ActionResponseDTO
     */
    @Override
    public ActionResponseDTO createExtension(OrderDTO extensionDTO, Long customerId, Long orderId, String customerIpAddress) {
        Customer customer = persistenceService.getCustomer(customerId);
        if (customer == null) { // An invalid customer ID was passed into parameter
            return createActionResponse(OrderStatus.ERROR.name(), "Customer for id[" + customerId + "] doesn't found.");
        }

        Order toExtend = findOrderForExtension(customer, orderId);
        if (toExtend == null) { // If a loan for extension wasn't found
            return createActionResponse(OrderStatus.ERROR.name(), "Order for id[" + orderId + "] doesn't found or invalid.");
        }

        int daysBetween = Days.daysBetween(new LocalDate(toExtend.getEndDate()), new LocalDate(extensionDTO.getStartDate())).getDays();
        if (daysBetween != 0) { // Loan's end date should be the same as extension's start date
            return createActionResponse(OrderStatus.ERROR.name(), "Extension has an invalid dates interval.");
        }

        Order extension = createExtensionFromOriginalOrder(customer, extensionDTO, toExtend, customerIpAddress);
        OrderDTO responseDTO = dozerBeanMapper.map(extension, OrderDTO.class);

        ActionResponseDTO actionResponse = createActionResponse(OrderStatus.SUCCESS.name(), "Extension created successfully.");
        actionResponse.setResponse(responseDTO);
        return actionResponse;
    }

    protected Order findOrderForExtension(Customer customer, Long orderId) {
        List<Order> existOrders = customer.getOrderList();
        for (Order existOrder : existOrders) {
            if (orderId.equals(existOrder.getId())) {
                if (!OrderStatus.SUCCESS.equals(existOrder.getOrderStatus())) {
                    break; // rejected order can't be extended
                } else if (!OrderType.LOAN.equals(existOrder.getOrderType())) {
                    break; // only loans can be extended
                }
                return existOrder;
            }
        }
        return null;
    }

    protected Order createExtensionFromOriginalOrder(Customer customer, OrderDTO extensionDTO, Order toExtend, String customerIpAddress) {
        BigDecimal modifiedOriginalOrderAmount = toExtend.getAmount().subtract(extensionDTO.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
        toExtend.setAmount(modifiedOriginalOrderAmount);

        BigDecimal extensionInterest = toExtend.getInterest().multiply(new BigDecimal("1.5")).setScale(2, BigDecimal.ROUND_HALF_UP);

        Order extension = dozerBeanMapper.map(extensionDTO, Order.class);
        extension.setCustomer(customer);
        extension.setCreateTime(GregorianCalendar.getInstance());
        extension.setOrderType(OrderType.EXTENSION);
        extension.setInterest(extensionInterest);
        extension.setCustomerIpAddress(customerIpAddress);
        extension.setOrderStatus(OrderStatus.SUCCESS);
        customer.getOrderList().add(extension);
        persistenceService.updateCustomer(customer);

        return extension;
    }

    @Override
    public String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public ActionResponseDTO createInvalidAttributeResponse(BindingResult result) {
        ActionResponseDTO response = createActionResponse(OrderStatus.ERROR.name(), "Some attributes are missing or invalid.");

        List<String> failedAttributes = new ArrayList<>();
        List<ObjectError> errors = result.getAllErrors();
        for (ObjectError error : errors) {
            FieldError fieldError = (FieldError) error;
            failedAttributes.add(fieldError.getField());
        }
        response.setResponse(failedAttributes);
        return response;
    }

    protected ActionResponseDTO createActionResponse(String status, String message) {
        ActionResponseDTO response = new ActionResponseDTO();
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }
}
