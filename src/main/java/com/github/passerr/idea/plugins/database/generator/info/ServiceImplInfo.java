package com.github.passerr.idea.plugins.database.generator.info;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

/**
 * 服务实现信息
 * @author xiehai
 * @date 2022/06/22 15:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceImplInfo extends ClassInfo {
}
