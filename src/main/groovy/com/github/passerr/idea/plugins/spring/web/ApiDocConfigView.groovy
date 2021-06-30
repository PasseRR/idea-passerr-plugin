package com.github.passerr.idea.plugins.spring.web

import com.github.passerr.idea.plugins.BaseTableModel
import com.github.passerr.idea.plugins.spring.web.po.ApiDocSettingPo
import com.intellij.openapi.util.Pair
import com.intellij.ui.PanelWithButtons
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable

import javax.swing.*
import java.awt.*

/**
 * @date 2021/06/30 17:22
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class ApiDocConfigView {
    private static JPanel queryParamPanel(ApiDocSettingPo setting) {
        JPanel panel = new JPanel(new BorderLayout())
        PanelWithButtons top = new IdeaPanelWithButtons() {
            @Override
            protected String getLabelText() {
                "忽略类型"
            }

            @Override
            protected JButton[] createButtons() {
                new JButton[0]
            }

            @Override
            protected JComponent createMainComponent() {
                def model = new BaseTableModel<String>(["类型"], setting.queryParamIgnoreTypes)
                JBTable table = new JBTable(model)
                table.with {
                    getEmptyText().setText("暂无数据")
                    getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                }

                ToolbarDecorator.createDecorator(table)
                    .setAddAction({ it -> })
                    .setAddActionName("新增")
                    .setEditAction({ it -> })
                    .setEditActionName("编辑")
                    .setRemoveAction({ it -> model.removeRow(table.getSelectedRow()) })
                    .setRemoveActionName("删除")
                    .disableUpDownActions()
                    .createPanel()
            }
        }
        PanelWithButtons bottom = new IdeaPanelWithButtons() {
            @Override
            protected String getLabelText() {
                "忽略注解"
            }

            @Override
            protected JButton[] createButtons() {
                new JButton[0]
            }

            @Override
            protected JComponent createMainComponent() {
                def model = new BaseTableModel<String>(["注解"], setting.queryParamIgnoreAnnotations)
                JBTable table = new JBTable(model)
                table.with {
                    getEmptyText().setText("暂无数据")
                    getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                }

                ToolbarDecorator.createDecorator(table)
                    .setAddAction({ it -> })
                    .setAddActionName("新增")
                    .setEditAction({ it -> })
                    .setEditActionName("编辑")
                    .setRemoveAction({ it -> model.removeRow(table.getSelectedRow()) })
                    .setRemoveActionName("删除")
                    .disableUpDownActions()
                    .createPanel()
            }
        }
        panel.add(top, BorderLayout.NORTH)
        panel.add(bottom, BorderLayout.CENTER)
        panel
    }

    static java.util.List<Pair<String, JPanel>> panels(ApiDocSettingPo setting) {
        [
            Pair.<String, JPanel> pair("Api模版", new JPanel()),
            Pair.<String, JPanel> pair("查询参数", queryParamPanel(setting))
        ]
    }

    abstract class IdeaPanelWithButtons extends PanelWithButtons {
        IdeaPanelWithButtons() {
            super()
            super.initPanel()
        }
    }
}
