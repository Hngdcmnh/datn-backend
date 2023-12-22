package com.mshop.orderservice.repository.entity.order;

public enum StatisticRevenueCondition {
    DAY("dd/MM/yyyy"),
    MONTH("MM/yyyy"),
    YEAR("yyyy");

    public final String format;

    StatisticRevenueCondition(String format) {
        this.format = format;
    }

    public static StatisticRevenueCondition fromString(String value) {
        if (value == null || value.isBlank()) return MONTH;
        switch (value.toUpperCase()) {
            case "DAY":
                return DAY;
            case "YEAR":
                return YEAR;
            default:
                return MONTH;
        }
    }
}
