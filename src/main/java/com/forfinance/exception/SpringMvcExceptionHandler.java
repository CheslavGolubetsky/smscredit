package com.forfinance.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@ControllerAdvice
@EnableWebMvc
@SuppressWarnings("unused")
public class SpringMvcExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SpringMvcExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SessionLocaleResolver localeResolver;

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(HttpServletRequest request, Exception ex) {
        Long exceptionUid = System.currentTimeMillis();
        LOG.error("Exception unique ID [" + exceptionUid + "]. ", ex);

        String errorHeader;
        if (ex instanceof SQLException) {
            errorHeader = messageSource.getMessage("error.message.db.exception", null, "", localeResolver.resolveLocale(request));
        } else {
            errorHeader = messageSource.getMessage("error.message.server.exception", null, "", localeResolver.resolveLocale(request));
        }

        Object statusCode = request.getAttribute("javax.servlet.error.status_code");
        String message = null;
        if (statusCode != null) {
            Integer httpStatusCode = (Integer) statusCode;
            message = messageSource.getMessage("error.message.code." + httpStatusCode, null, "", localeResolver.resolveLocale(request));
        }
        if (StringUtils.isEmpty(message)) {
            message = messageSource.getMessage("error.message.contact.support", null, "", localeResolver.resolveLocale(request));
        }

        ModelAndView model = new ModelAndView("servletException");
        model.addObject("errorHeader", errorHeader);
        model.addObject("errorMessage", message);
        model.addObject("errorTicket", String.valueOf(exceptionUid));

        return model;
    }
}
