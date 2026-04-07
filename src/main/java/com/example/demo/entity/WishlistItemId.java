package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class WishlistItemId implements Serializable {
    private static final long serialVersionUID = 3245973504725040247L;
    @NotNull
    @Column(name = "wishlist_id", nullable = false)
    private Integer wishlistId;

    @NotNull
    @Column(name = "product_variant_id", nullable = false)
    private Integer productVariantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WishlistItemId entity = (WishlistItemId) o;
        return Objects.equals(this.productVariantId, entity.productVariantId) &&
                Objects.equals(this.wishlistId, entity.wishlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productVariantId, wishlistId);
    }

}