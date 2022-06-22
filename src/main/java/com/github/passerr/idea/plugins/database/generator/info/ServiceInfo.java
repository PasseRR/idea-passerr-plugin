package com.github.passerr.idea.plugins.database.generator.info;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

/**
 * 服务接口信息
 * @author xiehai
 * @date 2022/06/22 14:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceInfo extends ClassInfo {
}
