package com.github.passerr.idea.plugins.spring.web.highlight;

import com.intellij.codeInsight.template.impl.TemplateColors;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * 模版语法高亮
 * @author xiehai
 * @date 2021/07/02 17:29
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class TemplateHighlighter extends SyntaxHighlighterBase {
    private final Lexer myLexer;

    public TemplateHighlighter() {
        myLexer = new MergingLexerAdapter(
            new FlexAdapter(new FileTemplateTextLexer()), TokenSet.create(FileTemplateTokenType.TEXT));
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return myLexer;
    }

    @Override
    @NotNull
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType == FileTemplateTokenType.MACRO || tokenType == FileTemplateTokenType.DIRECTIVE) {
            return pack(TemplateColors.TEMPLATE_VARIABLE_ATTRIBUTES);
        }

        return new TextAttributesKey[0];
    }
}