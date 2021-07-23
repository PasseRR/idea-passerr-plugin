package com.github.passerr.idea.plugins;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * {@link JBTable}重写
 * @author xiehai
 * @date 2021/07/01 12:47
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public class IdeaJbTable extends JBTable {
    public IdeaJbTable(TableModel model) {
        this(model, "暂无数据");
    }

    public IdeaJbTable(TableModel model, String empty) {
        this(model, empty, ListSelectionModel.SINGLE_SELECTION);
    }

    public IdeaJbTable(TableModel model, String empty, int selectModel) {
        super(model);
        super.getEmptyText().setText(empty);
        super.getSelectionModel().setSelectionMode(selectModel);
    }
}
