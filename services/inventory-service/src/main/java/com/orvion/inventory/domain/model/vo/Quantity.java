package com.orvion.inventory.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
public class Quantity {
    public static final int SCALE = 4;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private final BigDecimal value;
    private final String unit;
    
    public Quantity(BigDecimal value, String unit) {
        if (value == null || unit == null || unit.isBlank())
            throw new IllegalArgumentException("Quantity value and unit must not be null/empty");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Quantity cannot be negative");
        this.value = value.setScale(SCALE, ROUNDING);
        this.unit = unit;
    }
    
    public Quantity add(Quantity other) { validateSameUnit(other); return new Quantity(this.value.add(other.value), this.unit); }
    public Quantity subtract(Quantity other) { validateSameUnit(other); return new Quantity(this.value.subtract(other.value), this.unit); }
    public boolean isZero() { return value.compareTo(BigDecimal.ZERO) == 0; }
    public boolean isPositive() { return value.compareTo(BigDecimal.ZERO) > 0; }
    public static Quantity zero(String unit) { return new Quantity(BigDecimal.ZERO, unit); }
    private void validateSameUnit(Quantity other) { if (!this.unit.equals(other.unit)) throw new IllegalArgumentException("Unit mismatch"); }
}
