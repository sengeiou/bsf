package com.weking.model.pocket;

public class PlatformIncome {
    private Integer id;

    private Long dayDate;

    private Byte type;

    private Double amount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getDayDate() {
        return dayDate;
    }

    public void setDayDate(Long dayDate) {
        this.dayDate = dayDate;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}