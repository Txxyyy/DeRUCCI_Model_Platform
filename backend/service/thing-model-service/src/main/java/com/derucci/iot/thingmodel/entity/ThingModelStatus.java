package com.derucci.iot.thingmodel.entity;

/** 物模型状态枚举 */
public enum ThingModelStatus {
    /** 草稿 - 初始状态，可编辑 */
    DRAFT,
    /** 已发布 - 锁定状态，不可编辑 */
    PUBLISHED,
    /** 已废弃 - 不再使用 */
    DEPRECATED
}
