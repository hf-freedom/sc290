package com.member.storage.dto;

import com.member.storage.model.OrderItem;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class ConsumeRequest {
    @NotBlank(message = "账户ID不能为空")
    private String accountId;

    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    @NotNull(message = "消费金额不能为空")
    @Min(value = 0, message = "消费金额必须大于0")
    private Double totalAmount;

    private List<OrderItem> orderItems;
    private boolean useCoupon;
    private String couponId;
    private String remark;

    public ConsumeRequest() {
    }

    public ConsumeRequest(String accountId, String orderId, Double totalAmount,
                         List<OrderItem> orderItems, boolean useCoupon, String couponId, String remark) {
        this.accountId = accountId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.useCoupon = useCoupon;
        this.couponId = couponId;
        this.remark = remark;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "ConsumeRequest{" +
                "accountId='" + accountId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderItems=" + orderItems +
                ", useCoupon=" + useCoupon +
                ", couponId='" + couponId + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumeRequest that = (ConsumeRequest) o;
        return useCoupon == that.useCoupon &&
                Objects.equals(accountId, that.accountId) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(orderItems, that.orderItems) &&
                Objects.equals(couponId, that.couponId) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, orderId, totalAmount, orderItems, useCoupon, couponId, remark);
    }
}
