package com.github.passerr.idea.plugins.database.generator.action.template;

import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import com.github.passerr.idea.plugins.database.generator.action.DialogConfigInfo;
import com.github.passerr.idea.plugins.database.generator.config.ConfigPo;
import com.github.passerr.idea.plugins.naming.NamingStyle;
import com.github.passerr.idea.plugins.naming.NamingUtil;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.util.io.FileUtilRt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * 模版渲染
 * @author xiehai
 * @date 2022/06/27 15:30
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public enum Templates {
    /**
     * 实体模版
     */
    ENTITY(ConfigPo::getEntity, DialogConfigInfo::getEntityPackage) {
        @Override
        void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo) {
            EntityInfo entity = templateInfo.getEntity();
            entity.setPackageName(this.getPackage(configInfo));
            entity.setTableName(table.getName());
            entity.setTableComment(table.getComment());
            // 用于生成实体的表名
            String tableName = table.getName();
            if (!configInfo.getTablePrefix().isEmpty()
                && tableName.startsWith(configInfo.getTablePrefix())
                && tableName.length() > configInfo.getTablePrefix().length()) {
                tableName = tableName.substring(configInfo.getTablePrefix().length());
            }
            entity.setClassName(NamingUtil.toggle(NamingStyle.PASCAL, tableName));
            // TODO 设置列信息
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getEntity().getClassName());
        }
    },
    MAPPER(ConfigPo::getMapper, DialogConfigInfo::getMapperPackage) {
        @Override
        void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo) {
            MapperInfo mapper = templateInfo.getMapper();
            mapper.setPackageName(this.getPackage(configInfo));
            mapper.setClassName(
                String.format("%s%s", templateInfo.getEntity().getClassName(), configInfo.getMapperSuffix())
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getMapper().getClassName());
        }
    },
    MAPPER_XML(ConfigPo::getMapperXml, DialogConfigInfo::getMapperXmlPath, (a, b) -> b.isNeedMapperXml()) {
        @Override
        void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo) {
            // 不作处理
        }

        @Override
        String getPackage(DialogConfigInfo configInfo) {
            return
                Optional.of(configInfo)
                    .filter(it -> !it.isResourcesDir())
                    .map(it -> it.packages(this.packageFunction))
                    .orElseGet(configInfo::getMapperXmlPath);
        }

        @Override
        String getPath(DialogConfigInfo configInfo) {
            return
                Optional.of(configInfo)
                    .filter(it -> !it.isResourcesDir())
                    .map(it -> it.sourcePath(this.packageFunction))
                    .orElseGet(() -> configInfo.resourcePath(this.packageFunction));
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.xml", templateInfo.getMapper().getClassName());
        }
    },
    SERVICE(ConfigPo::getService, DialogConfigInfo::getServicePackage) {
        @Override
        void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo) {
            ServiceInfo service = templateInfo.getService();
            service.setPackageName(this.getPackage(configInfo));
            service.setClassName(String.format("%sService", templateInfo.getEntity().getClassName()));
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getService().getClassName());
        }
    },
    SERVICE_IMPL(ConfigPo::getServiceImpl, DialogConfigInfo::getServiceImplPackage, (a, b) -> a.isUseServiceImpl()) {
        @Override
        void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo) {
            ServiceImplInfo serviceImpl = templateInfo.getServiceImpl();
            serviceImpl.setPackageName(this.getPackage(configInfo));
            serviceImpl.setClassName(
                String.format("%s%s", templateInfo.getEntity().getClassName(), configInfo.getServiceImplSuffix())
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getServiceImpl().getClassName());
        }
    },
    CONTROLLER(ConfigPo::getController, DialogConfigInfo::getControllerPackage) {
        @Override
        void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo) {
            ControllerInfo controller = templateInfo.getController();
            controller.setPackageName(this.getPackage(configInfo));
            controller.setClassName(String.format("%sController", templateInfo.getEntity().getClassName()));
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getController().getClassName());
        }
    };

    Function<ConfigPo, StringBuilder> templateFunction;
    Function<DialogConfigInfo, String> packageFunction;
    BiPredicate<ConfigPo, DialogConfigInfo> validPredicate;

    Templates(Function<ConfigPo, StringBuilder> templateFunction,
              Function<DialogConfigInfo, String> packageFunction) {
        this(templateFunction, packageFunction, (po, info) -> true);
    }

    abstract void init(DasTable table, TemplateInfo templateInfo, DialogConfigInfo configInfo);

    String getPackage(DialogConfigInfo configInfo) {
        return configInfo.packages(this.packageFunction);
    }

    String getPath(DialogConfigInfo configInfo) {
        return configInfo.sourcePath(this.packageFunction);
    }

    abstract String getFileName(TemplateInfo templateInfo);

    public static void generate(Map<String, Object> map, DasTable table, ConfigPo configPo,
                                DialogConfigInfo configInfo) {
        TemplateInfo templateInfo = new TemplateInfo();
        templateInfo.fill(map);
        Arrays.stream(Templates.values()).forEach(it -> it.init(table, templateInfo, configInfo));
        Arrays.stream(Templates.values())
            .filter(it -> it.validPredicate.test(configPo, configInfo))
            .forEach(it -> {
                StringBuilder template = it.templateFunction.apply(configPo);
                String path = it.getPath(configInfo);
                String fileName = it.getFileName(templateInfo);
                File file = Paths.get(path, fileName).toFile();
                // 仅当允许文件覆盖或者文件不存在时写模版文件
                if (configInfo.isOverrideFile() || !file.exists()) {
                    FileUtilRt.createParentDirs(file);
                    if (!file.exists()) {
                        FileUtilRt.createIfNotExists(file);
                    }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        // 写文件
                        writer.write(VelocityUtil.format(template, map));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
    }
}
