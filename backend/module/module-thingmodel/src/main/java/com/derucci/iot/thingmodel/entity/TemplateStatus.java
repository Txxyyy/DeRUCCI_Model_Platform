package com.derucci.iot.thingmodel.entity;

/** 品类模板状态枚举 */
public enum TemplateStatus {
    /** 草稿 - 初始状态，可编辑 */
    DRAFT,
    /** 已发布 - 可被产品导入 */
    PUBLISHED,
    /** 已废弃 - 不再使用 */
    DEPRECATED
}
