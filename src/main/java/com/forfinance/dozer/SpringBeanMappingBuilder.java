package com.forfinance.dozer;

import com.forfinance.domain.Customer;
import com.forfinance.domain.Order;
import com.forfinance.dto.CustomerDTO;
import com.forfinance.dto.OrderDTO;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingOptions;

public class SpringBeanMappingBuilder extends BeanMappingBuilder {

    @Override
    protected void configure() {
        mapping(type(Customer.class), type(CustomerDTO.class),
                TypeMappingOptions.oneWay()
        );

        mapping(type(CustomerDTO.class), type(Customer.class),
                TypeMappingOptions.oneWay()
        ).exclude("id");

        mapping(type(Order.class), type(OrderDTO.class),
                TypeMappingOptions.oneWay()
        );

        mapping(type(OrderDTO.class), type(Order.class),
                TypeMappingOptions.oneWay()
        ).exclude("id").exclude("createTime").exclude("orderType");
    }
}