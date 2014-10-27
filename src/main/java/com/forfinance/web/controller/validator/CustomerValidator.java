package com.forfinance.web.controller.validator;

import com.forfinance.dto.CustomerDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CustomerValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "form.field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "form.field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "form.field.required");
    }
}
