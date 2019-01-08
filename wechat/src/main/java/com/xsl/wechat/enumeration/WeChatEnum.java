package com.xsl.wechat.enumeration;

public enum WeChatEnum {

    TOKEN("xsltoken4556"),

    APPID("wxf55b2f3af65b45dc"),
    APPSECRET("fa38bf41ce0eb2cddd33b09c757b9f08");

    private String value;

    WeChatEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
