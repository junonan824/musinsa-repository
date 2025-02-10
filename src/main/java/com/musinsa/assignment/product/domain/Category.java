package com.musinsa.assignment.product.domain;

public enum Category {
    TOP("상의"),
    OUTER("아우터"),
    PANTS("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("엑세서리");

    private final String korName;

    Category(String korName) {
        this.korName = korName;
    }

    public String getKorName() {
        return korName;
    }
}