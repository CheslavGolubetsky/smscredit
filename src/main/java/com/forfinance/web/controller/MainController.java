package com.forfinance.web.controller;

import com.forfinance.dto.ActionResponseDTO;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.HistoryDTO;
import com.forfinance.dto.OrderDTO;
import com.forfinance.exception.CustomerNotFoundException;
import com.forfinance.exception.InvalidCustomerDataException;
import com.forfinance.exception.OrderNotFoundException;
import com.forfinance.web.controller.validator.CustomerValidator;
import com.forfinance.web.controller.validator.OrderValidator;
import com.forfinance.web.service.MainApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Note that in Spring MVC 3.0 we had to explicitly use @Controller annotation
 * to specify controller servlet and @ResponseBody annotation in each and every method.
 * With the introduction of '@RestController' annotation in Spring MVC 4.0,
 * we can use it in place of @Controller and @ResponseBody annotation.
 */

@RestController
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private MainApplicationService applicationService;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private OrderValidator orderValidator;

    @RequestMapping(value = "/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomerDTO> getCustomers() {
        return applicationService.getCustomers();
    }

    @RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerDTO getCustomer(@PathVariable("customerId") Long customerId) {
        if (customerId == null || customerId < 1) {
            LOGGER.error("Requested an invalid parameter for customer search.");
            throw new CustomerNotFoundException();
        }

        CustomerDTO customer = applicationService.getCustomer(customerId);
        if (customer != null) {
            return customer;
        } else {
            LOGGER.error("No customer found with id [" + customerId + "].");
            throw new CustomerNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/customer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerDTO createCustomer(@ModelAttribute("customer") CustomerDTO customer, BindingResult result) {
        customerValidator.validate(customer, result);
        if (result.hasErrors()) {
            LOGGER.error("Customer can't be created due to invalid input parameters.");
            throw new InvalidCustomerDataException();
        }

        return applicationService.createCustomer(customer);
    }

    @RequestMapping(value = "/customer/{customerId}/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoryDTO getCustomerHistory(@PathVariable("customerId") Long customerId) {
        if (customerId == null || customerId < 1) {
            LOGGER.error("Requested an invalid parameter for customer search.");
            throw new CustomerNotFoundException();
        }

        HistoryDTO customerHistory = applicationService.getCustomerHistory(customerId);
        if (customerHistory != null) {
            return customerHistory;
        } else {
            LOGGER.error("No history found for customer with id [" + customerId + "].");
            throw new CustomerNotFoundException();
        }
    }

    /**
     * This method creates an order for defined customer.
     * Min.interest = 0.01
     * Min.possible amount = 10.00; max. possible amount = 500.00
     * Min.term is 2 days.
     * <p/>
     * Method throws a CustomerNotFoundException by passing an invalid parameter 'customerId'.
     * For valid data should be returned ActionResponseDTO for all cases.
     * When validation is not surpassed Http status will be OK(200), status - 'error',
     * otherwise Http status will be CREATED(201), status - 'success' and ActionResponseDTO contains a created object.
     *
     * @param order      - OrderDTO
     * @param customerId - Long
     * @param request    - Long
     * @param response   - HttpServletRequest
     * @param result     - HttpServletResponse
     * @return - ActionResponseDTO
     */
    @RequestMapping(value = "/customer/{customerId}/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponseDTO createOrder(@ModelAttribute("order") OrderDTO order, @PathVariable("customerId") Long customerId,
                                         HttpServletRequest request, HttpServletResponse response, BindingResult result) {
        if (customerId == null || customerId < 1) {
            LOGGER.error("Requested an invalid parameter for customer search.");
            throw new CustomerNotFoundException();
        }

        orderValidator.validate(order, result);
        if (result.hasErrors()) {
            return applicationService.createInvalidAttributeResponse(result);
        }

        String clientIpAddress = applicationService.getClientIpAddress(request);
        ActionResponseDTO responseDTO = applicationService.createOrder(order, customerId, clientIpAddress);
        if(responseDTO.getResponse() != null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        return responseDTO;
    }

    /**
     * This method creates an extension for an existing loan. Loan should be in status 'success'.
     * Extension start date should be the same as loan's end date.
     * Min.interest = 0.01
     * Min.possible amount = 10.00; max. possible amount = 500.00
     * Extension term is one week (7 days) only.
     * <p/>
     * Method throws a CustomerNotFoundException by passing an invalid parameter(s) 'customerId' or 'orderId'.
     * For valid data should be returned ActionResponseDTO for all cases.
     * When validation is not surpassed Http status will be OK(200), status - 'error',
     * otherwise Http status will be CREATED(201), status - 'success' and ActionResponseDTO contains a created object.
     *
     * @param order      - OrderDTO
     * @param customerId - Long
     * @param orderId    - Long
     * @param request    - HttpServletRequest
     * @param response   - HttpServletResponse
     * @param result     - BindingResult
     * @return - ActionResponseDTO
     */
    @RequestMapping(value = "/customer/{customerId}/order/{orderId}/extension", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ActionResponseDTO createExtension(@ModelAttribute("order") OrderDTO order, @PathVariable("customerId") Long customerId, @PathVariable("orderId") Long orderId,
                                             HttpServletRequest request, HttpServletResponse response, BindingResult result) {
        if (customerId == null || customerId < 1) {
            LOGGER.error("Requested an invalid parameter for customer search.");
            throw new CustomerNotFoundException();
        }
        if (orderId == null || orderId < 1) {
            LOGGER.error("Requested an invalid parameter for order search.");
            throw new OrderNotFoundException();
        }

        orderValidator.validate(order, result);
        orderValidator.validateExtension(order, result);
        if (result.hasErrors()) {
            return applicationService.createInvalidAttributeResponse(result);
        }

        String clientIpAddress = applicationService.getClientIpAddress(request);
        ActionResponseDTO responseDTO = applicationService.createExtension(order, customerId, orderId, clientIpAddress);
        if(responseDTO.getResponse() != null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        return responseDTO;
    }

}
