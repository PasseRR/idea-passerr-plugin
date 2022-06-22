package com.github.passerr.idea.plugins.database.generator.info;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

/**
 * mapper信息
 * @author xiehai
 * @date 2022/06/22 14:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MapperInfo extends ClassInfo {
}
