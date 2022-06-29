package com.github.passerr.idea.plugins.database.generator.template.po;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

/**
 * 代码生成参数设置
 * @author xiehai
 * @date 2022/06/24 16:41
 */
@Data
public class SettingPo {
    /**
     * 项目根路径
     */
    @Transient
    String basePath;
    /**
     * 表前缀
     */
    @Transient
    String tablePrefix;
    /**
     * 默认覆盖文件
     */
    @Transient
    boolean overrideFile = true;
    /**
     * 作者
     */
    String author = "generator";
    /**
     * 基础包名
     */
    String basePackage = StringConstants.EMPTY;
    /**
     * entity包名 默认entity
     */
    String entityPackage = "entity";
    /**
     * entity类后缀 默认空
     */
    String entitySuffix = StringConstants.EMPTY;
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
     * serviceImpl的包名
     */
    String serviceImplPackage = "service.impl";
    /**
     * serviceImpl的后缀
     */
    String serviceImplSuffix = "Impl";

    /**
     * 路径加上包路径后的代码目录路径
     * @param function 需要与{{@link #basePackage}}合并的路径
     * @return 合并后的路径
     */
    public String sourcePath(Function<SettingPo, String> function) {
        return
            DialogConfigUtil.mergePath(
                Paths.get(this.basePath, "src", "main", "java").toString(),
                this.basePackage,
                function.apply(this)
            );
    }

    /**
     * 路径加上包路径后的资源文件路径
     * @param function 资源目录
     * @return 合并后的路径
     */
    public String resourcePath(Function<SettingPo, String> function) {
        return
            DialogConfigUtil.mergePath(
                Paths.get(this.basePath, "src", "main", "resources").toString(),
                function.apply(this),
                StringConstants.EMPTY
            );
    }

    public String packages(Function<SettingPo, String> function) {
        return DialogConfigUtil.mergePackage(this.basePackage, function.apply(this));
    }

    public String getTablePrefix() {
        return Optional.ofNullable(tablePrefix).map(String::trim).orElse(StringConstants.EMPTY);
    }
}
