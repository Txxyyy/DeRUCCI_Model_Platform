package com.derucci.iot.product.entity;

/**
 * 产品状态枚举
 * PRD: 开发中(DEVELOPING) / 已发布(PUBLISHED)
 */
public enum ProductStatus {
    /** 开发中 - 产品创建后的初始状态，可编辑 */
    DEVELOPING,
    /** 已发布 - 产品经理点击发布后，信息锁定不可编辑 */
    PUBLISHED,
    /** 已下线 */
    OFFLINE,
    /** 已废弃 */
    DEPRECATED
}
