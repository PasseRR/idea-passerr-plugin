package com.github.passerr.idea.plugins.database.doc;

import com.github.passerr.idea.plugins.base.utils.NotificationUtil;
import com.intellij.database.model.DasObject;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jdesktop.swingx.JXTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileSystemView;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 导出设置窗口
 * @author xiehai
 * @date 2022/06/20 17:25
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportSettingDialog extends DialogWrapper {
    final List<DasObject> schemas;
    String title;
    String path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
    final AtomicInteger tableFields = new AtomicInteger();
    final AtomicInteger columnFields = new AtomicInteger();
    private static final TextBrowseFolderListener BROWSE_FOLDER_LISTENER;
    private static final String DEFAULT_TITLE = "数据库设计";

    static {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        descriptor.setShowFileSystemRoots(true);
        BROWSE_FOLDER_LISTENER = new TextBrowseFolderListener(descriptor);
    }

    protected ExportSettingDialog(List<DasObject> schemas) {
        super(true);
        super.setTitle("数据库设计文档导出");
        super.setOKButtonText("export");
        super.init();
        this.schemas = schemas;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JXTextField title = new JXTextField();
        title.setPrompt(DEFAULT_TITLE);
        title.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ExportSettingDialog.this.title = title.getText();
            }
        });
        panel.add(LabeledComponent.create(title, "   自定义标题", BorderLayout.WEST));

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
        FieldsHelper.tableFields(this.tableFields).forEach(tablePanel::add);
        panel.add(LabeledComponent.create(tablePanel, "   表字段选择", BorderLayout.WEST));


        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));
        FieldsHelper.columnFields(this.columnFields).forEach(columnPanel::add);
        panel.add(LabeledComponent.create(columnPanel, "   列字段选择", BorderLayout.WEST));

        TextFieldWithBrowseButton field = new TextFieldWithBrowseButton();
        field.setTextFieldPreferredWidth(50);
        field.setText(this.path);
        field.setEditable(false);
        field.addActionListener(e -> this.path = field.getText());
        field.addBrowseFolderListener(BROWSE_FOLDER_LISTENER);
        panel.add(LabeledComponent.create(field, "导出文档路径", BorderLayout.WEST));

        return panel;
    }

    @Override
    protected void doOKAction() {
        if (Objects.nonNull(this.schemas)) {
            try {
                DocExporter.export(
                    this.schemas,
                    Optional.ofNullable(this.title).filter(it -> !it.isEmpty()).orElse(DEFAULT_TITLE),
                    this.tableFields.get(),
                    this.columnFields.get(),
                    this.path
                );
            } finally {
                Notification notification = new Notification(
                    "数据库导出",
                    "数据库导出",
                    "数据库导出doc成功",
                    NotificationType.INFORMATION
                );
                notification.addAction(new NotificationAction("view") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                        try {
                            Desktop.getDesktop().open(new File(ExportSettingDialog.this.path));
                        } catch (IOException ignore) {
                        }
                    }
                });
                NotificationUtil.notify(notification);
            }
        }
        // 执行文档导出
        super.doOKAction();
    }
}
