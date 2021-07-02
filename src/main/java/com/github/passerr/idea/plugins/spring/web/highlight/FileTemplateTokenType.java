package com.github.passerr.idea.plugins.spring.web.highlight;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;

/**
 * com.intellij.ide.fileTemplates.impl.FileTemplateTokenType
 * @author xiehai
 * @date 2021/07/02 17:28
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public interface FileTemplateTokenType {
    IElementType TEXT = new IElementType("TEXT", Language.ANY);
    IElementType MACRO = new IElementType("MACRO", Language.ANY);
    IElementType DIRECTIVE = new IElementType("DIRECTIVE", Language.ANY);
}
