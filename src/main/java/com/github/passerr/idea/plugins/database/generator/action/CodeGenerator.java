package com.github.passerr.idea.plugins.database.generator.action;

import com.github.passerr.idea.plugins.database.generator.action.template.Templates;
import com.github.passerr.idea.plugins.database.generator.config.ConfigPo;
import com.intellij.database.model.DasTable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成工具
 * @author xiehai
 * @date 2022/06/27 13:49
 */
class CodeGenerator {
    static void generate(List<DasTable> list, ConfigPo configPo, DialogConfigInfo configInfo) {
        // 模版参数
        Map<String, Object> map = new HashMap<>(8);
        map.put("author", configPo.getAuthor());
        map.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        list.forEach(it -> Templates.generate(map, it, configPo, configInfo));
    }
}
