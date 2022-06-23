package com.github.passerr.idea.plugins.database.generator.config;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.DocumentAdapter;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;

/**
 * 同步弹窗
 * @author xiehai
 * @date 2022/06/23 19:23
 */
class SyncDialog extends DialogWrapper {
    final StringBuilder sb;
    String url;

    SyncDialog(StringBuilder sb) {
        super(true);
        this.sb = sb;
        super.setTitle("模版远程同步");
        super.setOKButtonText("同步");
        super.setCancelButtonText("取消");
        super.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JXTextField urlField = new JXTextField();
        urlField.setText(this.sb.toString());
        urlField.setColumns(35);
        urlField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                SyncDialog.this.url = urlField.getText();
            }
        });

        return LabeledComponent.create(urlField, "远程URL", BorderLayout.WEST);
    }
}
