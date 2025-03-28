package com.ragflow4j.server.enums;

/**
 * 应用发布渠道类型枚举
 */
public enum PublishChannelType {
    /**
     * 接口接入
     * 通过API接口方式接入应用
     */
    API,

    /**
     * 网页嵌入
     * 通过iframe或其他方式嵌入到网页中
     */
    WEB_EMBED,

    /**
     * 网页链接
     * 生成独立的网页访问链接
     */
    WEB_LINK,

    /**
     * 企业微信
     * 接入企业微信应用
     */
    WECOM,

    /**
     * 微信公众号
     * 接入微信公众号
     */
    WECHAT_MP
}