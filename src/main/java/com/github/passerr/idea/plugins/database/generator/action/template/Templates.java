package com.github.passerr.idea.plugins.database.generator.action.template;

import com.github.passerr.idea.plugins.base.constants.StringConstants;
import com.github.passerr.idea.plugins.base.utils.VelocityUtil;
import com.github.passerr.idea.plugins.database.generator.config.po.DetailPo;
import com.github.passerr.idea.plugins.database.generator.config.po.SettingPo;
import com.github.passerr.idea.plugins.database.generator.config.po.TemplatePo;
import com.github.passerr.idea.plugins.naming.NamingStyle;
import com.github.passerr.idea.plugins.naming.NamingUtil;
import com.github.passerr.idea.plugins.spring.web.Json5Generator;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtilRt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    ENTITY(DetailPo::getEntity, SettingPo::getEntityPackage) {
        @Override
        void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo, Map<String, String> types) {
            EntityInfo entity = templateInfo.getEntity();
            entity.setPackageName(this.getPackage(templatePo));
            entity.setTableName(table.getName());
            entity.setTableComment(table.getComment());
            // 用于生成实体的表名
            String tableName = table.getName(), tablePrefix = templatePo.getDetail().getSettings().getTablePrefix();
            if (StringUtils.isNotBlank(tablePrefix)
                && tableName.startsWith(tablePrefix = tablePrefix.trim())
                && tableName.length() > tablePrefix.length()) {
                tableName = tableName.substring(tablePrefix.length());
            }

            // 将去前缀的表名统一转为小写
            entity.setBaseName(NamingUtil.toggle(NamingStyle.PASCAL, tableName));
            entity.setClassName(
                entity.getBaseName() +
                    Optional.ofNullable(templatePo.getDetail().getSettings().getEntitySuffix())
                        .map(String::trim)
                        .orElse(StringConstants.EMPTY)
            );
            entity.setKebabName(NamingUtil.toggle(NamingStyle.LOWER_KEBAB, entity.getClassName()));
            entity.setSnakeName(NamingUtil.toggle(NamingStyle.LOWER_SNAKE, entity.getClassName()));

            DasUtil.getColumns(table)
                .map(it -> FieldInfo.of(table.getDataSource().getDatabaseVersion().name, it, types))
                .forEach(it -> {
                    entity.addField(it);
                    if (it.isPrimaryKey()) {
                        entity.setPk(it);
                    }
                });

            // 需要再初始化列后再设置导入包信息
            entity.setImports(
                TemplateUtil.imports(
                    entity.getFields().stream()
                        .map(FieldInfo::getFullClassName)
                        .filter(StringUtils::isNotBlank)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
                )
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getEntity().getClassName());
        }
    },
    MAPPER(DetailPo::getMapper, SettingPo::getMapperPackage) {
        @Override
        void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo, Map<String, String> types) {
            MapperInfo mapper = templateInfo.getMapper();
            mapper.setPackageName(this.getPackage(templatePo));
            mapper.setImports(TemplateUtil.imports(templateInfo.getEntity().getFullClassName()));
            mapper.setClassName(
                templateInfo.getEntity().getBaseName() +
                    Optional.ofNullable(templatePo.getDetail().getSettings())
                        .map(SettingPo::getMapperSuffix)
                        .map(String::trim)
                        .orElse(StringConstants.EMPTY)
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getMapper().getClassName());
        }
    },
    MAPPER_XML(DetailPo::getMapperXml, SettingPo::getMapperXmlPath, a -> a.getSettings().isNeedMapperXml()) {
        @Override
        void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo, Map<String, String> types) {
            // 不作处理
        }

        @Override
        String getPackage(TemplatePo templatePo) {
            SettingPo settings = templatePo.getDetail().getSettings();
            return
                Optional.of(settings)
                    .filter(it -> !it.isResourcesDir())
                    .map(it -> it.packages(this.packageFunction))
                    .orElseGet(settings::getMapperXmlPath);
        }

        @Override
        String getPath(TemplatePo templatePo) {
            SettingPo settings = templatePo.getDetail().getSettings();
            return
                Optional.of(settings)
                    .filter(it -> !it.isResourcesDir())
                    .map(it -> it.sourcePath(this.packageFunction))
                    .orElseGet(() -> settings.resourcePath(this.packageFunction));
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.xml", templateInfo.getMapper().getClassName());
        }
    },
    SERVICE(DetailPo::getService, SettingPo::getServicePackage) {
        @Override
        void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo, Map<String, String> types) {
            ServiceInfo service = templateInfo.getService();
            service.setPackageName(this.getPackage(templatePo));
            service.setClassName(
                templateInfo.getEntity().getBaseName() +
                    Optional.ofNullable(templatePo.getDetail().getSettings())
                        .map(SettingPo::getServiceSuffix)
                        .map(String::trim)
                        .orElse(StringConstants.EMPTY)
            );
            service.setImports(
                TemplateUtil.imports(
                    Arrays.asList(
                        templateInfo.getEntity().getFullClassName(),
                        templateInfo.getMapper().getFullClassName()
                    )
                )
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getService().getClassName());
        }
    },
    SERVICE_IMPL(DetailPo::getServiceImpl, SettingPo::getServiceImplPackage, DetailPo::isUseServiceImpl) {
        @Override
        void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo, Map<String, String> types) {
            ServiceImplInfo serviceImpl = templateInfo.getServiceImpl();
            serviceImpl.setPackageName(this.getPackage(templatePo));
            serviceImpl.setClassName(
                templateInfo.getService().getClassName() +
                    Optional.ofNullable(templatePo.getDetail().getSettings())
                        .map(SettingPo::getServiceImplSuffix)
                        .map(String::trim)
                        .orElse(StringConstants.EMPTY)
            );
            serviceImpl.setImports(
                TemplateUtil.imports(
                    Arrays.asList(
                        templateInfo.getEntity().getFullClassName(),
                        templateInfo.getMapper().getFullClassName(),
                        templateInfo.getService().getFullClassName()
                    )
                )
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getServiceImpl().getClassName());
        }
    },
    CONTROLLER(DetailPo::getController, SettingPo::getControllerPackage) {
        @Override
        void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo, Map<String, String> types) {
            ControllerInfo controller = templateInfo.getController();
            controller.setPackageName(this.getPackage(templatePo));
            controller.setClassName(
                templateInfo.getEntity().getBaseName() +
                    Optional.ofNullable(templatePo.getDetail().getSettings())
                        .map(SettingPo::getControllerSuffix)
                        .map(String::trim)
                        .orElse(StringConstants.EMPTY)
            );
            controller.setImports(
                TemplateUtil.imports(
                    Arrays.asList(
                        Optional.ofNullable(templateInfo.getEntity().getPk())
                            .map(FieldInfo::getPackageName)
                            .orElse(StringConstants.EMPTY),
                        templateInfo.getEntity().getFullClassName(),
                        templateInfo.getService().getFullClassName()
                    )
                )
            );
        }

        @Override
        String getFileName(TemplateInfo templateInfo) {
            return String.format("%s.java", templateInfo.getController().getClassName());
        }
    };

    Function<DetailPo, String> templateFunction;
    Function<SettingPo, String> packageFunction;
    Predicate<DetailPo> validPredicate;
    private static final Logger LOG = Logger.getInstance(Json5Generator.class);

    Templates(Function<DetailPo, String> templateFunction,
              Function<SettingPo, String> packageFunction) {
        this(templateFunction, packageFunction, t -> true);
    }

    abstract void init(DbTable table, TemplateInfo templateInfo, TemplatePo templatePo,
                       Map<String, String> types);

    String getPackage(TemplatePo templatePo) {
        return templatePo.getDetail().getSettings().packages(this.packageFunction);
    }

    String getPath(TemplatePo templatePo) {
        return templatePo.getDetail().getSettings().sourcePath(this.packageFunction);
    }

    abstract String getFileName(TemplateInfo templateInfo);

    public static void generate(Map<String, Object> map, DbTable table, TemplatePo templatePo,
                                Map<String, String> types) {
        TemplateInfo templateInfo = new TemplateInfo();
        templateInfo.fill(map);
        // 模版初始化
        Arrays.stream(Templates.values()).forEach(it -> it.init(table, templateInfo, templatePo, types));
        // 代码生成
        Arrays.stream(Templates.values())
            .filter(it -> it.validPredicate.test(templatePo.getDetail()))
            .forEach(it -> {
                StringBuilder template = new StringBuilder(it.templateFunction.apply(templatePo.getDetail()));
                String path = it.getPath(templatePo);
                String fileName = it.getFileName(templateInfo);
                File file = Paths.get(path, fileName).toFile();
                // 仅当允许文件覆盖或者文件不存在时写模版文件
                if (templatePo.getDetail().getSettings().isOverrideFile() || !file.exists()) {
                    FileUtilRt.createParentDirs(file);
                    if (!file.exists()) {
                        FileUtilRt.createIfNotExists(file);
                    }

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        // 写文件
                        writer.write(VelocityUtil.format(template, map));
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
    }
}
