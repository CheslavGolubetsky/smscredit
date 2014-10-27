package com.forfinance.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Entity
@NamedQuery(name = "Order.findByIpAndDate", query = "SELECT p FROM Order p WHERE p.customerIpAddress = ?1 AND (p.createTime BETWEEN ?2 AND ?3)")
@Table(name = "ORDERS")
@SuppressWarnings("unused")
public class Order implements Serializable {
    private static final long serialVersionUID = -6093543038852030357L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ID")
    private Long id;

    @Version
    @Column(name = "OPTIMISTIC_LOCK")
    private long version = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_TYPE", nullable = false)
    private OrderType orderType;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME", nullable = false)
    private Calendar createTime;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "END_DATE", nullable = false)
    private Date endDate;

    @Column(name = "INTEREST", nullable = false)
    private BigDecimal interest;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "CUSTOMER_IP_ADDRESS", nullable = false)
    private String customerIpAddress;

    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    public Long getId() {
        return id;
    }

    /**
     * This method should be used by Hibernate (and JUnit tests) only
     *
     * @param id Long
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomerIpAddress() {
        return customerIpAddress;
    }

    public void setCustomerIpAddress(String customerIpAddress) {
        this.customerIpAddress = customerIpAddress;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(customer).append(startDate).append(endDate);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof Order) {
            Order that = (Order) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.id, that.id);
            builder.append(this.customer, that.customer);
            builder.append(this.startDate, that.startDate);
            builder.append(this.endDate, that.endDate);
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
