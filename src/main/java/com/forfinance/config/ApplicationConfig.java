package com.forfinance.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.forfinance.dozer.SpringBeanMappingBuilder;
import com.forfinance.exception.ErrorHandlerFilter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@ComponentScan(basePackages = {"com.forfinance"})
@PropertySource(value = {"classpath:application.properties"})
//@EnableScheduling
//@EnableAspectJAutoProxy
@EnableWebMvc
@SuppressWarnings("unused")
public class ApplicationConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static ErrorHandlerFilter errorHandlerFilter() {
        return new ErrorHandlerFilter();
    }

    @Bean(name = "messageSource")
    public static ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("/WEB-INF/i18n/errors");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean(name = "localeResolver")
    public static SessionLocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    @Bean(name = "localeChangeInterceptor")
    public static LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    @Bean
    public static ControllerClassNameHandlerMapping controllerClassNameHandlerMapping(LocaleChangeInterceptor localeChangeInterceptor) {
        ControllerClassNameHandlerMapping classNameHandlerMapping = new ControllerClassNameHandlerMapping();
        classNameHandlerMapping.setInterceptors(new Object[]{localeChangeInterceptor});
        return classNameHandlerMapping;
    }

    @Bean(name = "dozerBeanMapper")
    public static DozerBeanMapper dozerBeanMapper() {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.addMapping(new SpringBeanMappingBuilder());
        return dozerBeanMapper;
    }

    @Bean(name = "objectMapper")
    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean(name = "httpClient")
    public static HttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

    @Bean(name = "restTemplate")
    public static RestTemplate restTemplate(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new org.springframework.http.converter.FormHttpMessageConverter());
        messageConverters.add(new org.springframework.http.converter.StringHttpMessageConverter());
        messageConverters.add(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }
}
