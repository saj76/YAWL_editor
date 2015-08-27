/*
 * Copyright (c) 2004-2014 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (c) 2004-2014 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.swing.JSingleSelectTable;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTable extends JSingleSelectTable {

    JComboBox _cbxType;  // reference to type combo - affects cell rendering

    public ConclusionTable(JComboBox cbxType, CellEditorListener listener) {
        super();
        _cbxType = cbxType;
        setModel(new ConclusionTableModel());
        setRowHeight(getRowHeight() + 5);
        setCellSelectionEnabled(true);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(true);
        setFillsViewportHeight(true);            // to allow drops on empty table
        getColumnModel().getColumn(1).setCellEditor(new ExletActionCellEditor(listener));
        getColumnModel().getColumn(2).setCellEditor(new ExletTargetCellEditor(listener));
        fixSelectorColumn();
    }


    public RdrConclusion getConclusion() { return getTableModel().getConclusion(); }


    public RdrPrimitive getSelectedPrimitive() {
        return getConclusion().getPrimitive(getSelectedRow() + 1);
    }


    public void setConclusion(List<RdrPrimitive> primitives) {
        getTableModel().setConclusion(primitives);
        updateUI();
    }


    public ConclusionTableModel getTableModel() {
        return (ConclusionTableModel) getModel();
    }


    public RuleType getSelectedRuleType() {
        return (RuleType) _cbxType.getSelectedItem();
    }


    public void setVisuals(boolean isValid) {
        Color bg = isValid ? Color.WHITE : Color.PINK;
        setBackground(bg);
    }


    public void addRow() {
        getTableModel().addRow();
        int row = getRowCount() - 1;
        selectRow(row);
        editCellAt(row, 0);
        requestFocusInWindow();
    }

    public void removeRow() {
        int row = getSelectedRow();
        getTableModel().removeRow(row);
        if (getRowCount() > 0) {
            selectRow((row < getRowCount() - 1) ? row : getRowCount() - 1);
        }
    }


    private void fixSelectorColumn() {
        TableColumn column = getColumnModel().getColumn(0);
        column.setPreferredWidth(15);
        column.setMaxWidth(15);
        column.setResizable(false);
    }


}
