package com.derucci.iot.ota.entity;

/** 固件状态枚举，表示固件在生命周期中的阶段 */
public enum FirmwareStatus {
    /** 草稿 - 新建未发布，可编辑 */
    DRAFT,
    /** 已发布 - 可用于OTA升级任务 */
    PUBLISHED,
    /** 已废弃 - 不再使用 */
    DEPRECATED
}
