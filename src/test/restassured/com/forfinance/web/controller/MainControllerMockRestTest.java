package com.forfinance.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.HistoryDTO;
import com.forfinance.dto.OrderDTO;
import com.forfinance.web.controller.validator.CustomerValidator;
import com.forfinance.web.service.MainApplicationService;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.jayway.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@RunWith(MockitoJUnitRunner.class)
public class MainControllerMockRestTest {

    @Mock
    @SuppressWarnings("unused")
    private MainApplicationService applicationService;

    @InjectMocks
    @SuppressWarnings("unused")
    private MainController mainController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(mainController);
    }

    @Test
    public void getCustomers() throws Exception {
        List<CustomerDTO> dtoList = new ArrayList<>();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(111L);
        customerDTO.setFirstName("Aaa");
        customerDTO.setLastName("Bbb");
        customerDTO.setCode("qwerty");
        dtoList.add(customerDTO);
        Mockito.doReturn(dtoList).when(applicationService).getCustomers();

        MockMvcResponse res = given().when().get("/customers");
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerDTO[] array = objectMapper.readValue(res.asString(), CustomerDTO[].class);
        assertEquals(1, array.length);
        assertEquals(Long.valueOf(111), array[0].getId());
        assertEquals("Aaa", array[0].getFirstName());
        assertEquals("Bbb", array[0].getLastName());
        assertEquals("qwerty", array[0].getCode());
    }

    @Test
    public void getCustomer() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(111L);
        customerDTO.setFirstName("Aaa");
        customerDTO.setLastName("Bbb");
        customerDTO.setCode("qwerty");
        Mockito.doReturn(customerDTO).when(applicationService).getCustomer(111L);

        given().
                when().
                get("/customer/111").
                then().
                statusCode(200).
                body("id", equalTo(111)).
                body("firstName", equalTo("Aaa")).
                body("lastName", equalTo("Bbb")).
                body("code", equalTo("qwerty"));
    }

    @Test
    public void createCustomer() throws Exception {
        CustomerValidator customerValidator = new CustomerValidator();
        injectService(mainController, customerValidator, "customerValidator", Autowired.class);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(111L);
        customerDTO.setFirstName("Aaa");
        customerDTO.setLastName("Bbb");
        customerDTO.setCode("qwerty");
        Mockito.doReturn(customerDTO).when(applicationService).createCustomer(Mockito.any(CustomerDTO.class));

        given().param("firstName", "A").param("lastName", "B").param("code", "100").
                when().
                post("/customer").
                then().
                statusCode(201).
                body("id", equalTo(111)).
                body("firstName", equalTo("Aaa")).
                body("lastName", equalTo("Bbb")).
                body("code", equalTo("qwerty"));
    }

    @Test
    public void getCustomerHistory() {
        HistoryDTO customerHistory = new HistoryDTO();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(111L);
        customerHistory.setCustomer(customerDTO);
        OrderDTO orderDTO_1 = new OrderDTO();
        orderDTO_1.setId(222L);
        customerHistory.getOrders().add(orderDTO_1);
        OrderDTO orderDTO_2 = new OrderDTO();
        orderDTO_2.setId(333L);
        customerHistory.getOrders().add(orderDTO_2);
        Mockito.doReturn(customerHistory).when(applicationService).getCustomerHistory(Long.valueOf("111"));

        given().
                when().
                get("/customer/111/history").
                then().
                statusCode(200).
                body("customer.id", equalTo(111)).
                body("orders.id", hasItems(222, 333));
    }

    void injectService(Object container, Object service, String serviceName, Class<? extends Annotation> annotationClass) throws Exception {
        Set<Field> autoWiredFields = new HashSet<>();
        Field[] fields = container.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                autoWiredFields.add(field);
            }
        }

        for (Field field : autoWiredFields) {
            String fieldName = field.getName();
            if (serviceName.equals(fieldName)) {
                field.setAccessible(true);
                field.set(container, service);
            }
        }
    }

}
