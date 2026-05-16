package com.member.storage.model;

import java.math.BigDecimal;

public class OrderItem {
    private String itemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String category;

    public OrderItem() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String itemId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
        private String category;

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public OrderItem build() {
            OrderItem item = new OrderItem();
            item.setItemId(itemId);
            item.setName(name);
            item.setPrice(price);
            item.setQuantity(quantity);
            item.setCategory(category);
            return item;
        }
    }
}
