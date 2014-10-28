package com.forfinance.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = {"com.forfinance.web"})
@EnableWebMvc
@SuppressWarnings("unused")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        super.configureHandlerExceptionResolvers(exceptionResolvers);

        exceptionResolvers.add(new DefaultHandlerExceptionResolver());
        exceptionResolvers.add(new ExceptionHandlerExceptionResolver());
        exceptionResolvers.add(new ResponseStatusExceptionResolver());

        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();

        Properties mappings = new Properties();
        mappings.put("org.springframework.web.servlet.PageNotFound", "404");
        mappings.put("org.springframework.dao.DataAccessException", "dataAccessFailure");
        mappings.put("org.springframework.transaction.TransactionException", "dataAccessFailure");
        simpleMappingExceptionResolver.setExceptionMappings(mappings);

        Properties statusCodes = new Properties();
        statusCodes.put("error/404", "404");
        statusCodes.put("error/error", "500");
        simpleMappingExceptionResolver.setStatusCodes(statusCodes);

        exceptionResolvers.add(simpleMappingExceptionResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);

        registry.addFormatterForFieldAnnotation(new CustomDateAnnotationFormatterFactory());
    }
}
