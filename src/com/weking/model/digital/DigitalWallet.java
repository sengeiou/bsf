package com.weking.model.digital;

import java.math.BigDecimal;

public class DigitalWallet {
    private Long id;

    private Long userId;

    private String symbol;

    private String address;

    private BigDecimal inAmount;

    private BigDecimal currAmount;

    private BigDecimal outAmount;

    private  String token_name;

    private  String token_logo;

    private  Double price;

    //剩余可提现数量
    private BigDecimal withdrawAmount;

    //所有可提现数量
    private BigDecimal allWithdrawAmount;

    public String getToken_name() {
        return token_name;
    }

    public void setToken_name(String token_name) {
        this.token_name = token_name;
    }

    public String getToken_logo() {
        return token_logo;
    }

    public void setToken_logo(String token_logo) {
        this.token_logo = token_logo;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public BigDecimal getInAmount() {
        return inAmount;
    }

    public void setInAmount(BigDecimal inAmount) {
        this.inAmount = inAmount;
    }

    public BigDecimal getCurrAmount() {
        return currAmount;
    }

    public void setCurrAmount(BigDecimal currAmount) {
        this.currAmount = currAmount;
    }

    public BigDecimal getOutAmount() {
        return outAmount;
    }

    public void setOutAmount(BigDecimal outAmount) {
        this.outAmount = outAmount;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public BigDecimal getAllWithdrawAmount() {
        return allWithdrawAmount;
    }

    public void setAllWithdrawAmount(BigDecimal allWithdrawAmount) {
        this.allWithdrawAmount = allWithdrawAmount;
    }
}