package com.forfinance.web.controller.validator;

import com.forfinance.dto.OrderDTO;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class OrderValidatorTest {
    private OrderValidator orderValidator;

    @Before
    public void setUp() throws Exception {
        orderValidator = new OrderValidator();
    }

    @Test
    public void testValidate() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setInterest(new BigDecimal("0.02"));
        dto.setAmount(new BigDecimal("100.00"));
        dto.setStartDate(new Date());
        dto.setEndDate(DateUtils.addDays(new Date(), 10));

        Errors errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validate(dto, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidate_fail() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setInterest(new BigDecimal("0.0001"));
        dto.setAmount(new BigDecimal("100000.00"));
        dto.setStartDate(DateUtils.addDays(new Date(), 10));
        dto.setEndDate(new Date());

        Errors errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validate(dto, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
        assertTrue(errors.hasFieldErrors("interest"));
        assertTrue(errors.hasFieldErrors("amount"));

        //
        Date date = new Date();
        dto = new OrderDTO();
        dto.setInterest(new BigDecimal("0.02"));
        dto.setAmount(new BigDecimal("1.00"));
        dto.setStartDate(date);
        dto.setEndDate(date);

        errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validate(dto, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
        assertTrue(errors.hasFieldErrors("amount"));
    }

    @Test
    public void testValidate_empty() throws Exception {
        OrderDTO dto = new OrderDTO();

        Errors errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validate(dto, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));
        assertTrue(errors.hasFieldErrors("endDate"));
        assertTrue(errors.hasFieldErrors("interest"));
        assertTrue(errors.hasFieldErrors("amount"));
    }

    @Test
    public void validateExtension() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setStartDate(DateUtils.addDays(new Date(), 10));
        dto.setEndDate(new Date());

        Errors errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validateExtension(dto, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));

        //
        dto.setStartDate(new Date());
        dto.setEndDate(DateUtils.addDays(new Date(), 5));
        errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validateExtension(dto, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));

        //
        dto.setStartDate(new Date());
        dto.setEndDate(DateUtils.addDays(new Date(), 10));
        errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validateExtension(dto, errors);
        assertTrue(errors.hasErrors());
        assertTrue(errors.hasFieldErrors("startDate"));

        //
        dto.setStartDate(new Date());
        dto.setEndDate(DateUtils.addDays(new Date(), 7));
        errors = new BeanPropertyBindingResult(dto, "interest");
        orderValidator.validateExtension(dto, errors);
        assertFalse(errors.hasErrors());
    }
}
