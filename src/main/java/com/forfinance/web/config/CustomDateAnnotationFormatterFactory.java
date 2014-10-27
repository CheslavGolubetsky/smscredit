package com.forfinance.web.config;


import com.forfinance.web.controller.formatter.CustomDateFormatter;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CustomDateAnnotationFormatterFactory implements AnnotationFormatterFactory<CustomDate> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        Set<Class<?>> setTypes = new HashSet<>();
        setTypes.add(Date.class);
        return setTypes;
    }

    @Override
    public Printer<?> getPrinter(CustomDate annotation, Class<?> fieldType) {
        return new CustomDateFormatter();
    }

    @Override
    public Parser<?> getParser(CustomDate annotation, Class<?> fieldType) {
        return new CustomDateFormatter();
    }
}
