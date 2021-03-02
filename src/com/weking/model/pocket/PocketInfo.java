package com.weking.model.pocket;

import java.math.BigDecimal;

public class PocketInfo {
    private Integer id;

    private Integer userId;

    private Long totalTicket;

    private Integer totalDiamond;

    private Integer freeDiamond;

    private Double totalMoney;

    private  Long all_diamond;

    private BigDecimal sca_gold;

    private BigDecimal all_sca_gold;


    public Long getAll_diamond() {
        return all_diamond;
    }

    public void setAll_diamond(Long all_diamond) {
        this.all_diamond = all_diamond;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getTotalTicket() {
        return totalTicket;
    }

    public void setTotalTicket(Long totalTicket) {
        this.totalTicket = totalTicket;
    }

    public Integer getTotalDiamond() {
        return totalDiamond;
    }

    public void setTotalDiamond(Integer totalDiamond) {
        this.totalDiamond = totalDiamond;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getSca_gold() {
        return sca_gold;
    }

    public void setSca_gold(BigDecimal sca_gold) {
        this.sca_gold = sca_gold;
    }

    public BigDecimal getAll_sca_gold() {
        return all_sca_gold;
    }

    public void setAll_sca_gold(BigDecimal all_sca_gold) {
        this.all_sca_gold = all_sca_gold;
    }

    public Integer getFreeDiamond() {
        return freeDiamond;
    }

    public void setFreeDiamond(Integer freeDiamond) {
        this.freeDiamond = freeDiamond;
    }
}