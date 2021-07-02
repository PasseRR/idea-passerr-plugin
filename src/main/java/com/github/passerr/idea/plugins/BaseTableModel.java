package com.github.passerr.idea.plugins;

import com.intellij.util.ui.ItemRemovable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * {@link javax.swing.table.TableModel}双向绑定
 * @author xiehai
 * @date 2021/06/30 18:50
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseTableModel<T> extends AbstractTableModel implements ItemRemovable {
    final List<T> data;
    final List<String> headers;

    public BaseTableModel(List<String> headers, List<T> data) {
        this.headers = headers;
        this.data = data;
    }

    @Override
    public String getColumnName(int column) {
        return this.headers.get(column);
    }

    @Override
    public void removeRow(int idx) {
        if (idx >= 0 && idx < this.getRowCount()) {
            this.data.remove(idx);
            super.fireTableRowsDeleted(idx, idx);
        }
    }

    @Override
    public int getRowCount() {
        return this.data.size();
    }

    @Override
    public int getColumnCount() {
        return this.columns().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.columns().get(columnIndex).apply(this.data.get(rowIndex));
    }

    /**
     * 列属性
     * @return 列
     */
    protected List<Function<T, Object>> columns() {
        return Collections.singletonList(it -> it);
    }
}
