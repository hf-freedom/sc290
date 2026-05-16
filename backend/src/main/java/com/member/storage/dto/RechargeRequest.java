package com.member.storage.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class RechargeRequest {
    @NotBlank(message = "账户ID不能为空")
    private String accountId;

    @NotNull(message = "充值金额不能为空")
    @Min(value = 0, message = "充值金额必须大于0")
    private Double amount;

    private String operatorId;
    private String remark;

    public RechargeRequest() {
    }

    public RechargeRequest(String accountId, Double amount, String operatorId, String remark) {
        this.accountId = accountId;
        this.amount = amount;
        this.operatorId = operatorId;
        this.remark = remark;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "RechargeRequest{" +
                "accountId='" + accountId + '\'' +
                ", amount=" + amount +
                ", operatorId='" + operatorId + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RechargeRequest that = (RechargeRequest) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, amount, operatorId, remark);
    }
}
