package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class OrderItemId implements Serializable {
    private static final long serialVersionUID = 3321002764064903374L;
    @Column(name = "order_item_id", nullable = false)
    private Integer orderItemId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Override
    public String toString() {
        return "OrderItemId{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderItemId entity = (OrderItemId) o;
        return Objects.equals(this.orderId, entity.orderId) &&
                Objects.equals(this.orderItemId, entity.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderItemId);
    }

}