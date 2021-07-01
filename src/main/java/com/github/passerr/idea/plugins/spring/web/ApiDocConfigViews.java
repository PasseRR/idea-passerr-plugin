package com.github.passerr.idea.plugins.spring.web;

import com.github.passerr.idea.plugins.BaseTableModel;
import com.github.passerr.idea.plugins.IdeaJbTable;
import com.github.passerr.idea.plugins.IdeaPanelWithButtons;
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.PanelWithButtons;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * api文档配置视图
 * @author xiehai
 * @date 2021/06/30 19:39
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
public abstract class ApiDocConfigViews {
    /**
     * 根据已有配置生成视图
     * @param setting {@link ApiDocSettingPo}
     * @return {@link List}
     */
    public static java.util.List<Pair<String, JPanel>> panels(ApiDocSettingPo setting) {
        return
            Arrays.asList(
                Pair.pair("Api模版", new JPanel()),
                Pair.pair("查询参数", queryParamPanel(setting))
            );
    }

    /**
     * 查询参数视图
     * @param setting {@link ApiDocSettingPo}
     * @return {@link JPanel}
     */
    private static JPanel queryParamPanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new GridBagLayout());
        PanelWithButtons top = new IdeaPanelWithButtons() {
            @Override
            protected String getLabelText() {
                return "忽略类型";
            }

            @Override
            protected JButton[] createButtons() {
                return new JButton[0];
            }

            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("类型"), setting.getQueryParamIgnoreTypes());
                JBTable table = new IdeaJbTable(model);

                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it -> {})
                        .setAddActionName("新增")
                        .setEditAction(it -> {})
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .disableUpDownActions()
                        .createPanel();
            }
        };
        PanelWithButtons bottom = new IdeaPanelWithButtons() {
            @Override
            protected String getLabelText() {
                return "忽略注解";
            }

            @Override
            protected JButton[] createButtons() {
                return new JButton[0];
            }

            @Override
            protected JComponent createMainComponent() {
                BaseTableModel<String> model = new BaseTableModel<>(
                    Collections.singletonList("注解"), setting.getQueryParamIgnoreAnnotations());
                JBTable table = new IdeaJbTable(model);

                return
                    ToolbarDecorator.createDecorator(table)
                        .setAddAction(it -> {})
                        .setAddActionName("新增")
                        .setEditAction(it -> {})
                        .setEditActionName("编辑")
                        .setRemoveAction(it -> model.removeRow(table.getSelectedRow()))
                        .setRemoveActionName("删除")
                        .disableUpDownActions()
                        .createPanel();
            }
        };
        panel.add(
            top,
            new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );
        panel.add(
            bottom,
            new GridBagConstraints(
                0, 1, 1, 1, 1, 1,
                GridBagConstraints.NORTH,
                GridBagConstraints.BOTH,
                JBUI.emptyInsets(), 0, 0
            )
        );

        return panel;
    }
}
