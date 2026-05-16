package com.member.storage.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class CombinePayRequest {
    @NotBlank(message = "账户ID不能为空")
    private String accountId;

    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    @NotNull(message = "总金额不能为空")
    @Min(value = 0, message = "总金额必须大于0")
    private Double totalAmount;

    private List<PaymentItem> paymentItems;
    private String remark;

    public CombinePayRequest() {
    }

    public CombinePayRequest(String accountId, String orderId, Double totalAmount,
                           List<PaymentItem> paymentItems, String remark) {
        this.accountId = accountId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.paymentItems = paymentItems;
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

    public List<PaymentItem> getPaymentItems() {
        return paymentItems;
    }

    public void setPaymentItems(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "CombinePayRequest{" +
                "accountId='" + accountId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentItems=" + paymentItems +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CombinePayRequest that = (CombinePayRequest) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(totalAmount, that.totalAmount) &&
                Objects.equals(paymentItems, that.paymentItems) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, orderId, totalAmount, paymentItems, remark);
    }

    public static class PaymentItem {
        private String type;
        private Double amount;
        private String couponId;

        public PaymentItem() {
        }

        public PaymentItem(String type, Double amount, String couponId) {
            this.type = type;
            this.amount = amount;
            this.couponId = couponId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCouponId() {
            return couponId;
        }

        public void setCouponId(String couponId) {
            this.couponId = couponId;
        }

        @Override
        public String toString() {
            return "PaymentItem{" +
                    "type='" + type + '\'' +
                    ", amount=" + amount +
                    ", couponId='" + couponId + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PaymentItem that = (PaymentItem) o;
            return Objects.equals(type, that.type) &&
                    Objects.equals(amount, that.amount) &&
                    Objects.equals(couponId, that.couponId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, amount, couponId);
        }
    }
}
