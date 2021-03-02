package com.weking.model.PlayAddress;

public class PlayAddress {
    private Integer id;

    private String account;

    private String rtmpurl;

    private String hlsurl;

    private String hdlurl;
    
    private String picurl;

    public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRtmpurl() {
        return rtmpurl;
    }

    public void setRtmpurl(String rtmpurl) {
        this.rtmpurl = rtmpurl == null ? null : rtmpurl.trim();
    }

    public String getHlsurl() {
        return hlsurl;
    }

    public void setHlsurl(String hlsurl) {
        this.hlsurl = hlsurl == null ? null : hlsurl.trim();
    }

    public String getHdlurl() {
        return hdlurl;
    }

    public void setHdlurl(String hdlurl) {
        this.hdlurl = hdlurl == null ? null : hdlurl.trim();
    }
}