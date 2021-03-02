package com.weking.model.digital;

public class DigitalToken {
    private Integer id;

    private String symbol;

    private String tokenAddress;

    private String tokenName;

    private String tokenLogo;

    private Short isValid;

    private Double price;

    private String groups;

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress == null ? null : tokenAddress.trim();
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName == null ? null : tokenName.trim();
    }

    public String getTokenLogo() {
        return tokenLogo;
    }

    public void setTokenLogo(String tokenLogo) {
        this.tokenLogo = tokenLogo == null ? null : tokenLogo.trim();
    }

    public Short getIsValid() {
        return isValid;
    }

    public void setIsValid(Short isValid) {
        this.isValid = isValid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}