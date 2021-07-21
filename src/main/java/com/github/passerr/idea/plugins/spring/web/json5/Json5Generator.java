package com.github.passerr.idea.plugins.spring.web.json5;

import com.github.passerr.idea.plugins.spring.web.AliasType;
import com.github.passerr.idea.plugins.spring.web.SpringWebPsiUtil;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocObjectSerialPo;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.CommonClassNames;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    // 忽略注解
    private final Set<String> ignores;
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
        collect.putIfAbsent("boolean", new ApiDocObjectSerialPo("boolean", AliasType.BOOLEAN.getType(), "true"));
        Arrays.asList("byte", "short", "char", "int")
            .forEach(it -> collect.putIfAbsent(it, new ApiDocObjectSerialPo(it, AliasType.INT.getType(), "1024")));
        Arrays.asList("float", "double")
            .forEach(it -> collect.putIfAbsent(it, new ApiDocObjectSerialPo(it, AliasType.FLOAT.getType(), "102.4")));
        collect.putIfAbsent("long", new ApiDocObjectSerialPo("long", AliasType.STRING.getType(), "2048"));
        this.serials = Collections.unmodifiableMap(collect);
    }

    /**
     * 是否是忽略类型
     * @param psiType {@link PsiType}
     * @return true/false
     */
    boolean isIgnore(PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType) {
            // void类型忽略
            if ("void".equals(((PsiPrimitiveType) psiType).getName())) {
                return true;
            }
            return false;
        }

        PsiClass resolve = ((PsiClassType) psiType).resolve();
        if (Objects.isNull(resolve)) {
            return false;
        }

        // java.lang.Void类型忽略
        return CommonClassNames.JAVA_LANG_VOID.equals(resolve.getQualifiedName());
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

        this.toJson5(writer, psiType);

        return Optional.of(stringWriter.toString()).map(String::trim).filter(it -> it.length() > 0).orElse(null);
    }

    void toJson5(Json5Writer writer, PsiType type) {
        if (this.isIgnore(type)) {
            return;
        }

        // 数组类型
        if (type instanceof PsiArrayType) {
            PsiArrayType psiArrayType = (PsiArrayType) type;
            BEGIN_ARRAY.accept(writer);
            this.toJson5(writer, psiArrayType.getComponentType());
            this.toJson5(writer, psiArrayType.getComponentType());
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
        if (CommonClassNames.JAVA_LANG_OBJECT.equals(className)) {
            BEGIN_OBJECT.accept(writer);
            END_OBJECT.accept(writer);
            return;
        }

        // 基本类型
        if (serials.containsKey(className)) {
            ApiDocObjectSerialPo po = serials.get(className);
            VALUE.accept(writer, AliasType.value(po.getAlias(), po.getValue()));
            return;
        }

        // 否则则为复杂类型
        if (type instanceof PsiClassReferenceType) {
            PsiClassReferenceType referenceType = (PsiClassReferenceType) type;
            // 泛型参数
            Map<PsiTypeParameter, PsiType> substitutionMap = referenceType.resolveGenerics().getSubstitutor()
                .getSubstitutionMap();

            // 集合类型
            if (InheritanceUtil.isInheritor(type, Collection.class.getName())) {
                BEGIN_ARRAY.accept(writer);
                // 若存在泛型参数
                if (referenceType.getParameterCount() > 0) {
                    this.toJson5(writer, referenceType.getParameters()[0]);
                    this.toJson5(writer, referenceType.getParameters()[0]);
                }
                END_ARRAY.accept(writer);
                return;
            }

            BEGIN_OBJECT.accept(writer);
            // 接口类型、枚举类型不序列化
            if (!psiClass.isInterface() && !psiClass.isEnum()) {
                Arrays.stream(psiClass.getAllFields())
                    // 非static、transient字段
                    .filter(SpringWebPsiUtil::isValidFiled)
                    // 非注解标记字段
                    .filter(it -> AnnotationUtil.findAnnotations(it, this.ignores).length == 0)
                    .sorted(Comparator.comparing(PsiField::getName))
                    .forEach(it -> {
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
                        // TODO 处理泛型和递归调用问题
                        this.toJson5(writer, it.getType());
                    });
            }
            END_OBJECT.accept(writer);
        }
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
}
