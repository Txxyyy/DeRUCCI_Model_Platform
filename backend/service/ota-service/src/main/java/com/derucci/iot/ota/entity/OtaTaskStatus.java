package com.derucci.iot.ota.entity;

/** OTA升级任务状态枚举 */
public enum OtaTaskStatus {
    /** 待执行 - 任务已创建，等待启动 */
    PENDING,
    /** 执行中 - 正在向设备推送固件 */
    RUNNING,
    /** 已完成 - 所有设备升级结束 */
    COMPLETED,
    /** 已取消 - 手动终止任务 */
    CANCELLED
}
