package com.github.passerr.idea.plugins.spring.web.json5;

import com.github.passerr.idea.plugins.spring.web.po.ApiDocObjectSerialPo;
import com.intellij.psi.PsiType;

import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

/**
 * json5工具类
 * @author xiehai
 * @date 2021/07/20 14:13
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface Json5Util {
    /**
     * 将任意类型转为json5
     * @param psiType     {@link PsiType}
     * @param rootComment 根注释
     * @param ignores     忽略类型
     * @param objects     序列化支持类型
     * @return json5
     */
    static String toJson5(PsiType psiType, String rootComment, List<String> ignores,
                          List<ApiDocObjectSerialPo> objects) {
        StringWriter stringWriter = new StringWriter();
        Json5Writer writer = Json5Writer.json5(stringWriter);
        writer.setIndent("  ");
        try {
            if (Objects.nonNull(rootComment) && !rootComment.isEmpty()) {
                writer.comment(rootComment);
            }
            doToJson5(writer, psiType);
        } catch (Exception ignore) {

        }
        return stringWriter.toString();
    }

    static void doToJson5(Json5Writer writer, PsiType psiType) {
        // TODO 完成json5序列化
    }
}
