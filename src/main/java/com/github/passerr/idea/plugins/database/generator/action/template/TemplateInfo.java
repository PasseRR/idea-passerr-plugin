package com.github.passerr.idea.plugins.database.generator.action.template;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Map;

/**
 * 模版变量信息
 * @author xiehai
 * @date 2022/06/27 15:24
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class TemplateInfo {
    EntityInfo entity = new EntityInfo();
    MapperInfo mapper = new MapperInfo();
    ServiceInfo service = new ServiceInfo();
    ServiceImplInfo serviceImpl = new ServiceImplInfo();
    ControllerInfo controller = new ControllerInfo();

    /**
     * 填充模版参数
     * @param map 声明的参数map
     */
    void fill(Map<String, Object> map) {
        Arrays.stream(TemplateInfo.class.getDeclaredFields())
            .forEach(it -> {
                try {
                    map.put(it.getName(), it.get(this));
                } catch (IllegalAccessException ignore) {
                }
            });
    }
}
