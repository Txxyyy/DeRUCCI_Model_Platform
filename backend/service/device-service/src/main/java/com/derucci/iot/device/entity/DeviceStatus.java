package com.derucci.iot.device.entity;

/** 设备状态枚举，表示设备的连接生命周期 */
public enum DeviceStatus {
    /** 在线 - 设备已连接并正常通信 */
    ONLINE,
    /** 离线 - 设备已断开连接 */
    OFFLINE,
    /** 未激活 - 设备已注册但从未上线 */
    UNACTIVE
}
