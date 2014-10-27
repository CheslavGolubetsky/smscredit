package com.forfinance.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Resources: <p/>
 * English: http://en.wikipedia.org/wiki/List_of_HTTP_status_codes <p/>
 * Russian: http://ru.wikipedia.org/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA_%D0%BA%D0%BE%D0%B4%D0%BE%D0%B2_%D1%81%D0%BE%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D1%8F_HTTP
 */

public class ErrorHandlerFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlerFilter.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SessionLocaleResolver localeResolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        Object statusCode = request.getAttribute("javax.servlet.error.status_code");
        String message = null;
        if (statusCode != null) {
            Integer httpStatusCode = (Integer) statusCode;
            message = messageSource.getMessage("error.message.code." + httpStatusCode, null, "", localeResolver.resolveLocale(request));
        }
        if (StringUtils.isEmpty(message)) {
            message = messageSource.getMessage("error.message.contact.support", null, "", localeResolver.resolveLocale(request));
        }

        long exceptionUid = System.currentTimeMillis();
        String errorHeader = messageSource.getMessage("error.message.server.exception", null, "", localeResolver.resolveLocale(request));
        if (statusCode != null) {
            errorHeader = errorHeader + " - " + statusCode;
        }
        request.setAttribute("errorHeader", errorHeader);
        request.setAttribute("errorMessage", message);
        request.setAttribute("errorTicket", String.valueOf(exceptionUid));

        try {
            LOG.error("Exception unique ID [" + exceptionUid + "]. Error code [" + statusCode + "]. ");
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (Exception ex) {
            LOG.error("Exception unique ID [" + exceptionUid + "]. Error code [" + statusCode + "]. ", ex);
            request.getRequestDispatcher("/WEB-INF/jsp/servletException.jsp").forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to initialize
    }

    @Override
    public void destroy() {
        // no need to destroy
    }
}