package com.github.passerr.idea.plugins

import com.intellij.util.ui.ItemRemovable

import javax.swing.table.AbstractTableModel

/**
 * @date 2021/06/30 11:03
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class BaseTableModel<T> extends AbstractTableModel implements ItemRemovable {
    List<T> data
    List<String> headers

    BaseTableModel(List<String> headers, List<T> data) {
        this.headers = headers
        this.data = data
    }

    @Override
    String getColumnName(int column) {
        this.headers[column]
    }

    @Override
    void removeRow(int idx) {
        if (idx >= 0 && idx < this.getRowCount()) {
            this.data.remove(idx as int)
            super.fireTableRowsDeleted(idx, idx)
        }
    }

    @Override
    int getRowCount() {
        this.data.size()
    }

    @Override
    int getColumnCount() {
        this.isSelf() ? 1 : this.columns().size()
    }

    @Override
    Object getValueAt(int rowIndex, int columnIndex) {
        this.isSelf() ? this.data[rowIndex] : this.data.properties[this.columns()[columnIndex]]
    }

    /**
     * 列属性
     * @return 列
     */
    List<String> columns() {
        []
    }

    /**
     * 列表元素是否是列本身
     * @return true/false
     */
    boolean isSelf() {
        true
    }
}
