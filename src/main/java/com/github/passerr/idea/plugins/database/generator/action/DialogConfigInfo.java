package com.github.passerr.idea.plugins.database.generator.action;

import lombok.Data;

/**
 * 弹窗配置信息
 * @author xiehai
 * @date 2022/06/24 16:41
 */
@Data
public class DialogConfigInfo {
    /**
     * 项目根路径
     */
    String basePath;
    /**
     * 基础包名
     */
    String basePackage;
    /**
     * entity包名 默认entity
     */
    String entityPackage = "entity";
    /**
     * entity类后缀 默认空
     */
    String entitySuffix;
    /**
     * mapper包名 默认mapper
     */
    String mapperPackage = "mapper";
    /**
     * mapper后缀 默认Mapper
     */
    String mapperSuffix = "Mapper";
    /**
     * 是否生成mapperXml
     */
    boolean needMapperXml = true;
    /**
     * 是否是资源目录
     */
    boolean resourcesDir = true;
    /**
     * mapper路径 默认为mapper
     */
    String mapperXmlPath = "mapper";
    /**
     * service包名 默认service
     */
    String servicePackage = "service";
    /**
     * controller包名 默认controller
     */
    String controllerPackage = "controller";

    DialogConfigInfo(String basePath) {
        this.basePath = basePath;
    }
}
