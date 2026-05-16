package com.member.storage.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class RefundRequest {
    @NotBlank(message = "消费记录ID不能为空")
    private String consumeRecordId;

    @NotNull(message = "退款金额不能为空")
    @Min(value = 0, message = "退款金额必须大于0")
    private Double refundAmount;

    @NotBlank(message = "操作员ID不能为空")
    private String operatorId;

    private String reason;

    public RefundRequest() {
    }

    public RefundRequest(String consumeRecordId, Double refundAmount, String operatorId, String reason) {
        this.consumeRecordId = consumeRecordId;
        this.refundAmount = refundAmount;
        this.operatorId = operatorId;
        this.reason = reason;
    }

    public String getConsumeRecordId() {
        return consumeRecordId;
    }

    public void setConsumeRecordId(String consumeRecordId) {
        this.consumeRecordId = consumeRecordId;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "RefundRequest{" +
                "consumeRecordId='" + consumeRecordId + '\'' +
                ", refundAmount=" + refundAmount +
                ", operatorId='" + operatorId + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefundRequest that = (RefundRequest) o;
        return Objects.equals(consumeRecordId, that.consumeRecordId) &&
                Objects.equals(refundAmount, that.refundAmount) &&
                Objects.equals(operatorId, that.operatorId) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumeRecordId, refundAmount, operatorId, reason);
    }
}
