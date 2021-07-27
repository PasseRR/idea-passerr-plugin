package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.spring.web.json5.Json5Writer;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocObjectSerialPo;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTypesUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * json5工具类
 * @author xiehai
 * @date 2021/07/20 14:13
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class Json5Generator {
    /**
     * 字段忽略注解
     */
    private final Set<String> ignores;
    /**
     * 字段序列化
     */
    private final Map<String, ApiDocObjectSerialPo> serials;
    private static final Logger LOG = Logger.getInstance(Json5Generator.class);
    private static final Consumer<Json5Writer> BEGIN_ARRAY = writer -> {
        try {
            writer.beginArray();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final Consumer<Json5Writer> END_ARRAY = writer -> {
        try {
            writer.endArray();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final Consumer<Json5Writer> BEGIN_OBJECT = writer -> {
        try {
            writer.beginObject();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final Consumer<Json5Writer> END_OBJECT = writer -> {
        try {
            writer.endObject();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final BiConsumer<Json5Writer, String> COMMENT = (writer, s) -> {
        try {
            writer.comment(s);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final BiConsumer<Json5Writer, Object> VALUE = (writer, o) -> {
        try {
            writer.value(o);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };
    private static final BiConsumer<Json5Writer, String> NAME = (writer, s) -> {
        try {
            writer.name(s);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    };

    Json5Generator(List<String> originIgnores, List<ApiDocObjectSerialPo> originSerials) {
        // object不能在校验范围
        this.ignores = Collections.unmodifiableSet(new HashSet<>(originIgnores));
        Map<String, ApiDocObjectSerialPo> collect = originSerials.stream()
            .collect(Collectors.toMap(ApiDocObjectSerialPo::getType, it -> it, (o, n) -> n));
        // 保证存在原型类型
        WebCopyConstants.PRIMITIVE_SERIALS.forEach(it -> collect.putIfAbsent(it.getType(), it.deepCopy()));
        this.serials = Collections.unmodifiableMap(collect);
    }

    /**
     * 将任意类型转为json5
     * @param psiType       {@link PsiType}
     * @param rootComment   根注释
     * @param originIgnores 忽略类型
     * @param originSerials 序列化支持类型
     * @return json5
     */
    public static String toJson5(PsiType psiType, String rootComment, List<String> originIgnores,
                                 List<ApiDocObjectSerialPo> originSerials) {
        return new Json5Generator(originIgnores, originSerials).toJson5(psiType, rootComment);
    }

    /**
     * 是否是忽略类型
     * @param psiType {@link PsiType}
     * @return true/false
     */
    boolean isIgnore(PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType) {
            // void类型忽略
            return void.class.getName().equals(((PsiPrimitiveType) psiType).getName());
        }

        if (!(psiType instanceof PsiClassType)) {
            return false;
        }

        PsiClass resolve = ((PsiClassType) psiType).resolve();
        // java.lang.Void类型忽略
        return resolve != null && Void.class.getName().equals(resolve.getQualifiedName());
    }

    String toJson5(PsiType psiType, String rootComment) {
        if (this.isIgnore(psiType)) {
            return null;
        }

        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");

        // 根注释
        if (Objects.nonNull(rootComment) && !rootComment.trim().isEmpty()) {
            COMMENT.accept(writer, rootComment.trim());
        }

        this.toJson5(writer, psiType, "$");

        return Optional.of(stringWriter.toString()).map(String::trim).filter(it -> it.length() > 0).orElse(null);
    }

    void toJson5(Json5Writer writer, PsiType type, String ref) {
        if (this.isIgnore(type)) {
            return;
        }

        // 数组类型
        if (type instanceof PsiArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) type;
            BEGIN_ARRAY.accept(writer);
            this.toJson5(writer, psiArrayType.getComponentType(), ref + "[0]");
            this.toJson5(writer, psiArrayType.getComponentType(), ref + "[1]");
            END_ARRAY.accept(writer);
            return;
        }

        if (type instanceof PsiPrimitiveType) {
            // 基本类型
            PsiPrimitiveType primitiveType = (PsiPrimitiveType) type;
            ApiDocObjectSerialPo po = serials.get(primitiveType.getName());
            VALUE.accept(writer, AliasType.value(po.getAlias(), po.getValue()));
            return;
        }

        PsiClass psiClass = PsiTypesUtil.getPsiClass(type);
        // 不支持类型
        if (Objects.isNull(psiClass)) {
            return;
        }
        String className = psiClass.getQualifiedName();
        // 基本类型
        if (serials.containsKey(className)) {
            ApiDocObjectSerialPo po = serials.get(className);
            VALUE.accept(writer, AliasType.value(po.getAlias(), po.getValue()));
            return;
        }

        // 否则则为复杂类型
        if (type instanceof PsiClassReferenceType) {
            PsiClassReferenceType referenceType = (PsiClassReferenceType) type;
            // 集合类型
            if (InheritanceUtil.isInheritor(type, Iterable.class.getName())) {
                BEGIN_ARRAY.accept(writer);
                // 若存在泛型参数
                if (referenceType.getParameterCount() > 0) {
                    this.toJson5(writer, referenceType.getParameters()[0], ref + "<0>");
                    this.toJson5(writer, referenceType.getParameters()[0], ref + "<1>");
                }
                END_ARRAY.accept(writer);
                return;
            }

            BEGIN_OBJECT.accept(writer);
            // 接口类型、枚举类型不序列化
            if (!psiClass.isInterface() && !psiClass.isEnum()) {
                // 当前class泛型参数
                Map<PsiTypeParameter, PsiType> substitutionMap = new HashMap<>(
                    referenceType.resolveGenerics().getSubstitutor().getSubstitutionMap()
                );

                // 若存在父类 添加父类的的泛型
                PsiClassType[] extendsListTypes = psiClass.getExtendsListTypes();
                for (PsiClassType extendsListType : extendsListTypes) {
                    substitutionMap.putAll(extendsListType.resolveGenerics().getSubstitutor().getSubstitutionMap());
                }

                Arrays.stream(psiClass.getAllFields())
                    // 非static、transient字段
                    .filter(SpringWebPsiUtil::isValidFiled)
                    // 非注解标记字段
                    .filter(it -> AnnotationUtil.findAnnotations(it, this.ignores).length == 0)
                    // 去掉父类重复字段
                    .collect(Collectors.toMap(PsiField::getName, it -> it, (o, n) -> o))
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(PsiField::getName))
                    .forEach(it -> this.fieldJson5(writer, it, substitutionMap, ref));
            }
            END_OBJECT.accept(writer);
        }
    }

    private void fieldJson5(Json5Writer writer, PsiField it, Map<PsiTypeParameter, PsiType> substitutionMap,
                            String ref) {
        // 字段注释
        Optional.ofNullable(it.getDocComment())
            .map(PsiDocComment::getDescriptionElements)
            .map(els ->
                Arrays.stream(els)
                    .filter(e -> e instanceof PsiDocToken)
                    .map(PsiDocToken.class::cast)
                    .filter(SpringWebPsiUtil::isDocCommentData)
                    .map(e -> e.getText().trim())
                    .collect(Collectors.joining(""))
            )
            .filter(comment -> !comment.isEmpty())
            .ifPresent(comment -> COMMENT.accept(writer, comment));
        NAME.accept(writer, it.getName());
        PsiType fieldType = it.getType();

        String suffix = String.format("@%s:%s", it.getType().getCanonicalText(), it.getName());
        // 引用循环
        if (ref.contains(suffix)) {
            if (InheritanceUtil.isInheritor(fieldType, Iterable.class.getName())
                || fieldType instanceof PsiArrayType) {
                BEGIN_ARRAY.accept(writer);
                END_ARRAY.accept(writer);
            } else {
                BEGIN_OBJECT.accept(writer);
                END_OBJECT.accept(writer);
            }
            return;
        }

        // 包装类型
        if (fieldType instanceof PsiClassReferenceType) {
            PsiClassReferenceType paramClassReferenceType = (PsiClassReferenceType) fieldType;
            // 集合类型
            if (InheritanceUtil.isInheritor(fieldType, Iterable.class.getName())) {
                BEGIN_ARRAY.accept(writer);
                // 若存在泛型参数
                if (paramClassReferenceType.getParameterCount() > 0
                    && paramClassReferenceType.getParameters()[0] instanceof PsiClassReferenceType) {
                    PsiClassType parameter = (PsiClassType) paramClassReferenceType.getParameters()[0];
                    PsiClass resolve = parameter.resolve();
                    if (resolve instanceof PsiTypeParameter && substitutionMap.containsKey(resolve)) {
                        this.toJson5(writer, substitutionMap.get(resolve), ref + suffix + "<0>");
                        this.toJson5(writer, substitutionMap.get(resolve), ref + suffix + "<1>");
                    } else {
                        this.toJson5(writer, parameter, ref + suffix + "<0>");
                        this.toJson5(writer, parameter, ref + suffix + "<1>");
                    }
                }
                END_ARRAY.accept(writer);
                return;
            }

            PsiClass paramType = paramClassReferenceType.resolve();
            if (paramType instanceof PsiTypeParameter && substitutionMap.containsKey(paramType)) {
                PsiType psiType = substitutionMap.get(paramType);
                // 存在泛型类型为null的情况
                if (psiType != null) {
                    this.toJson5(writer, psiType, ref + suffix);
                    // 提前结束泛型参数处理
                    return;
                }
            }
        }

        this.toJson5(writer, fieldType, ref + suffix);
    }
}
