package com.github.passerr.idea.plugins.database.generator.template.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 弹窗配置信息
 * @author xiehai
 * @date 2022/06/24 16:41
 */
@Data
public class DialogConfigPo implements Serializable {
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
     * service后缀
     */
    String serviceSuffix = "Service";
    /**
     * controller包名 默认controller
     */
    String controllerPackage = "controller";
    /**
     * controller后缀
     */
    String controllerSuffix = "Controller";
    /**
     * 是否使用serviceImpl
     */
    boolean useServiceImpl = false;
    /**
     * serviceImpl的包名
     */
    String serviceImplPackage = "service.impl";
    /**
     * serviceImpl的后缀
     */
    String serviceImplSuffix = "Impl";
    /**
     * 默认覆盖文件
     */
    boolean overrideFile = true;
    /**
     * 作者
     */
    String author;
    /**
     * 表前缀
     */
    String tablePrefix;
}
