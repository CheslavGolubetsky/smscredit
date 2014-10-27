package com.forfinance.web.controller.formatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomDateFormatter implements Formatter<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public Date parse(String s, Locale locale) throws ParseException {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        try {
            return dateFormat.parse(s);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public String print(Date date, Locale locale) {
        if (date == null) {
            return null;
        }

        return dateFormat.format(date);
    }
}
