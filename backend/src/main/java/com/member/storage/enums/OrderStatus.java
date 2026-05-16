package com.member.storage.enums;

public enum OrderStatus {
    SUCCESS("成功"),
    PENDING("待处理"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    FAILED("失败"),
    CANCELLED("已取消"),
    REFUNDED("已退款"),
    PARTIAL_REFUND("部分退款");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
