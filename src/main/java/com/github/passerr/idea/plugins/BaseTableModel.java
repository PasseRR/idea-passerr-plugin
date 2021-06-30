package com.github.passerr.idea.plugins;

import com.intellij.util.ui.ItemRemovable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiehai
 * @date 2021/06/30 18:50
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseTableModel<T> extends AbstractTableModel implements ItemRemovable {
    final List<T> data;
    final List<String> headers;

    BaseTableModel(List<String> headers, List<T> data) {
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
        return this.columns().isEmpty() ? 1 : this.columns().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = this.data.get(rowIndex);
        if (this.columns().isEmpty()) {
            return value;
        }

        // TODO
        return null;
    }

    /**
     * 列属性
     * @return 列
     */
    List<String> columns() {
        return new ArrayList<>();
    }
}
