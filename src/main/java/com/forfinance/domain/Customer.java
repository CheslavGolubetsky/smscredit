package com.forfinance.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMERS")
@SuppressWarnings("unused")
public class Customer implements Serializable {
    private static final long serialVersionUID = -6972807252396546397L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CUSTOMER_ID")
    private Long id;

    @Version
    @Column(name = "OPTIMISTIC_LOCK")
    private long version = 0;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "CODE", nullable = false)
    private String code;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Order.class, mappedBy = "customer")
    private List<Order> orderList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    /**
     * This method should be used by Hibernate (and JUnit tests) only
     *
     * @param id - Long
     */
    public void setId(Long id) {
        this.id = id;
    }


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(code).append(firstName).append(lastName);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof Customer) {
            Customer that = (Customer) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.code, that.code);
            builder.append(this.firstName, that.firstName);
            builder.append(this.lastName, that.lastName);
            return builder.isEquals();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
