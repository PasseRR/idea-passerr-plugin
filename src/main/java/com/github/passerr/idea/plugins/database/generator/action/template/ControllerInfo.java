package com.github.passerr.idea.plugins.database.generator.action.template;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * controller信息
 * @author xiehai
 * @date 2022/06/27 15:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControllerInfo extends ClassInfo {
    List<String> parameterImports;

    ControllerInfo() {
    }
}
