package com.forfinance.web.controller;

import com.forfinance.dto.ActionResponseDTO;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.OrderDTO;
import com.forfinance.exception.CustomerNotFoundException;
import com.forfinance.web.controller.validator.OrderValidator;
import com.forfinance.web.service.MainApplicationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MainControllerTest {

    @Mock
    @SuppressWarnings("unused")
    private MainApplicationService applicationService;

    @Mock
    @SuppressWarnings("unused")
    private OrderValidator orderValidator;

    @Mock
    @SuppressWarnings("unused")
    private HttpServletRequest request;

    @Mock
    @SuppressWarnings("unused")
    private HttpServletResponse response;

    @Mock
    @SuppressWarnings("unused")
    private BindingResult bindingResult;

    @InjectMocks
    @SuppressWarnings("unused")
    private MainController mainController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCustomers() {
        List<CustomerDTO> dtoList = new ArrayList<>();
        Mockito.doReturn(dtoList).when(applicationService).getCustomers();

        List<CustomerDTO> result = mainController.getCustomers();
        assertSame(dtoList, result);

        Mockito.verify(applicationService).getCustomers();
    }

    @Test
    public void getCustomers_RequestMapping() throws Exception {
        RequestMapping requestMapping = getMethodAnnotation(RequestMapping.class, MainController.class, "getCustomers");
        assertEquals("/customers", requestMapping.value()[0]);
        assertEquals(RequestMethod.GET, requestMapping.method()[0]);
        assertEquals(MediaType.APPLICATION_JSON_VALUE, requestMapping.produces()[0]);
    }

    @Test
    public void getCustomer() {
        try {
            mainController.getCustomer(Long.valueOf("-1"));
        } catch (CustomerNotFoundException e) {
            Mockito.verify(applicationService, VerificationModeFactory.times(0)).getCustomer(1L);
        } catch (Exception e) {
            fail();
        }

        //
        Mockito.doReturn(null).when(applicationService).getCustomer(1L);
        try {
            mainController.getCustomer(1L);
        } catch (CustomerNotFoundException e) {
            Mockito.verify(applicationService, VerificationModeFactory.times(1)).getCustomer(1L);
        } catch (Exception e) {
            fail();
        }

        //
        CustomerDTO customer = new CustomerDTO();
        Mockito.doReturn(customer).when(applicationService).getCustomer(1L);
        CustomerDTO result = mainController.getCustomer(1L);
        assertSame(customer, result);

        Mockito.verify(applicationService, VerificationModeFactory.times(2)).getCustomer(1L);
    }

    @Test
    public void createOrder() {
        OrderDTO order = new OrderDTO();
        Long customerId = null;
        try {
            mainController.createOrder(order, customerId, request, response, bindingResult);
        } catch (CustomerNotFoundException e) {
            Mockito.verify(applicationService, VerificationModeFactory.times(0)).createOrder(Mockito.any(OrderDTO.class), Mockito.anyLong(), Mockito.anyString());
        } catch (Exception e) {
            fail();
        }

        //
        Mockito.doNothing().when(orderValidator).validate(order, bindingResult);
        Mockito.doReturn(true).when(bindingResult).hasErrors();
        ActionResponseDTO invalid = new ActionResponseDTO();
        Mockito.doReturn(invalid).when(applicationService).createInvalidAttributeResponse(bindingResult);
        customerId = Long.valueOf("111");

        ActionResponseDTO result = mainController.createOrder(order, customerId, request, response, bindingResult);
        assertSame(result, invalid);
        Mockito.verify(orderValidator, VerificationModeFactory.times(1)).validate(order, bindingResult);
        Mockito.verify(bindingResult, VerificationModeFactory.times(1)).hasErrors();
        Mockito.verify(applicationService, VerificationModeFactory.times(1)).createInvalidAttributeResponse(bindingResult);
        Mockito.verify(applicationService, VerificationModeFactory.times(0)).getClientIpAddress(request);
        Mockito.verify(applicationService, VerificationModeFactory.times(0)).createOrder(Mockito.any(OrderDTO.class), Mockito.anyLong(), Mockito.anyString());

        //
        Mockito.reset(orderValidator, bindingResult, applicationService);
        Mockito.doReturn(false).when(bindingResult).hasErrors();
        Mockito.doReturn("clientIpAddress").when(applicationService).getClientIpAddress(request);
        ActionResponseDTO responseDTO = new ActionResponseDTO();
        responseDTO.setResponse("HereShouldBeDTO");
        Mockito.doReturn(responseDTO).when(applicationService).createOrder(order, customerId, "clientIpAddress");

        result = mainController.createOrder(order, customerId, request, response, bindingResult);
        assertSame(result, responseDTO);
        Mockito.verify(orderValidator, VerificationModeFactory.times(1)).validate(order, bindingResult);
        Mockito.verify(bindingResult, VerificationModeFactory.times(1)).hasErrors();
        Mockito.verify(applicationService, VerificationModeFactory.times(0)).createInvalidAttributeResponse(bindingResult);
        Mockito.verify(applicationService, VerificationModeFactory.times(1)).getClientIpAddress(request);
        Mockito.verify(applicationService, VerificationModeFactory.times(1)).createOrder(order, customerId, "clientIpAddress");
        Mockito.verify(response, VerificationModeFactory.times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    protected <T extends Annotation> T getMethodAnnotation(Class<T> aClass, Class<?> ctrClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassCastException {
        Method method = ctrClass.getMethod(methodName, parameterTypes);
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(aClass)) {
                return aClass.cast(annotation);
            }
        }
        return null;
    }

}
