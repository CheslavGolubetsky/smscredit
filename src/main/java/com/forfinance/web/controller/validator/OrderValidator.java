package com.forfinance.web.controller.validator;

import com.forfinance.dto.OrderDTO;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Component
public class OrderValidator implements Validator {
    public static final BigDecimal MIN_INTEREST = new BigDecimal("0.01");
    public static final BigDecimal MIN_POSSIBLE_AMOUNT = new BigDecimal("10.00");
    public static final BigDecimal MAX_POSSIBLE_AMOUNT = new BigDecimal("500.00");
    public static final int MIN_TERM_DAYS = 3;

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "form.field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", "form.field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "interest", "form.field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "form.field.required");

        if (!errors.hasErrors()) {
            OrderDTO dto = (OrderDTO) target;

            if (dto.getStartDate().after(dto.getEndDate())) {
                errors.rejectValue("startDate", "form.field.required");
            } else {
                int days = Days.daysBetween(new LocalDate(dto.getStartDate()), new LocalDate(dto.getEndDate())).getDays();
                if (days < MIN_TERM_DAYS) {
                    errors.rejectValue("startDate", "form.field.required");
                }
            }

            if (MIN_INTEREST.compareTo(dto.getInterest()) >= 0) {
                errors.rejectValue("interest", "form.field.required");
            }

            if (MIN_POSSIBLE_AMOUNT.compareTo(dto.getAmount()) >= 0 || MAX_POSSIBLE_AMOUNT.compareTo(dto.getAmount()) < 0) {
                errors.rejectValue("amount", "form.field.required");
            }
        }
    }

    /**
     * Mandatory validation should be performed before this method calls.
     * Extension term is one week (7 days) only.
     *
     * @param dto    - OrderDTO
     * @param errors - Errors
     */
    public void validateExtension(OrderDTO dto, Errors errors) {
        if (!errors.hasErrors()) {
            int days = Days.daysBetween(new LocalDate(dto.getStartDate()), new LocalDate(dto.getEndDate())).getDays();
            if (days != 7) {
                errors.rejectValue("startDate", "form.field.required");
            }
        }
    }
}
