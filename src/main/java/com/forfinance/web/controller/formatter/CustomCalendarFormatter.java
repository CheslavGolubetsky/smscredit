package com.forfinance.web.controller.formatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomCalendarFormatter implements Formatter<Calendar> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public Calendar parse(String s, Locale locale) throws ParseException {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        try {
            final Date date = dateFormat.parse(s);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public String print(Calendar calendar, Locale locale) {
        if (calendar == null) {
            return null;
        }

        return dateFormat.format(calendar.getTime());
    }
}
