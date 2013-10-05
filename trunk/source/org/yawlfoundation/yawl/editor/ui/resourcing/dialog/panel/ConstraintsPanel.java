package org.yawlfoundation.yawl.editor.ui.resourcing.dialog.panel;

import org.yawlfoundation.yawl.editor.core.resourcing.BasicOfferInteraction;
import org.yawlfoundation.yawl.editor.ui.resourcing.dialog.ResourceDialog;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.resourcing.constraints.AbstractConstraint;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

/**
 * @author Michael Adams
 * @date 23/05/13
 */
public class ConstraintsPanel extends JPanel implements ItemListener {

    private JCheckBox chkFourEyes;
    private JComboBox cbxFourEyes;
    private JCheckBox chkFamTask;
    private JComboBox cbxFamTask;

    private static final String FOUR_EYES_NAME = "SeparationOfDuties";
    private static final String FOUR_EYES_PARAM_NAME = "familiarTask";


    public ConstraintsPanel(ResourceDialog owner, Set<YAtomicTask> preTasks) {
        setBorder(new TitledBorder("Constraints"));
        addContent(owner, preTasks);
        setPreferredSize(new Dimension(425, 145));
    }


    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        boolean selected = e.getStateChange() == ItemEvent.SELECTED;
        if (source == chkFourEyes) {
            cbxFourEyes.setEnabled(selected);
        }
        else if (source == chkFamTask) {
            cbxFamTask.setEnabled(selected);
        }
    }

    public void load(BasicOfferInteraction offerInteraction) {
        String famTask = offerInteraction.getFamiliarParticipantTask();
        if (famTask != null) {
            chkFamTask.setSelected(selectItem(cbxFamTask, famTask));
            if (! chkFamTask.isSelected()) {
                showMissingTaskWarning("familiar task", famTask);
            }
        }
        cbxFamTask.setEnabled(chkFamTask.isSelected());

        for (AbstractConstraint constraint : offerInteraction.getConstraintSet().getAll()) {
            if (constraint.getName().equals(FOUR_EYES_NAME)) {
                famTask = constraint.getParamValue(FOUR_EYES_PARAM_NAME);
                if (famTask != null) {
                    chkFourEyes.setSelected(selectItem(cbxFourEyes, famTask));
                    if (! chkFourEyes.isSelected()) {
                        showMissingTaskWarning("separation of duties", famTask);
                    }
                }
            }
        }
        cbxFourEyes.setEnabled(chkFourEyes.isSelected());

        disableIfNoPresetTasks();
    }


    public void save(BasicOfferInteraction offerInteraction) {
        offerInteraction.getConstraintSet().clear();
        String famTask = null;
        String fourEyesTask = null;
        if (chkFamTask.isSelected()) {
            famTask = getSelectedTaskID(cbxFamTask);
            if (famTask != null) {
                offerInteraction.setFamiliarParticipantTask(famTask);
            }
        }
        if (chkFourEyes.isSelected()) {
            fourEyesTask = getSelectedTaskID(cbxFourEyes);
            if (fourEyesTask != null) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(FOUR_EYES_PARAM_NAME, fourEyesTask);
                offerInteraction.getConstraintSet().add(FOUR_EYES_NAME, params);
            }
        }
        if (famTask != null && famTask.equals(fourEyesTask)) {
            showClashWarning(famTask);
        }
    }


    private void addContent(ResourceDialog owner, Set<YAtomicTask> preTasks) {
        setLayout(new BorderLayout());
        add(createFamTaskPanel(owner, preTasks), BorderLayout.CENTER);
        add(createFourEyesPanel(owner, preTasks), BorderLayout.SOUTH);
    }


    private JPanel createFamTaskPanel(ResourceDialog owner, Set<YAtomicTask> preTasks) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(24, 5, 8, 5));
        chkFamTask = createCheckBox("Choose completer(s) of task:", owner);
        cbxFamTask = createCombo(owner, preTasks);
        panel.add(chkFamTask, BorderLayout.CENTER);
        panel.add(cbxFamTask, BorderLayout.EAST);
        panel.setSize(410, 25);
        return panel;
    }


    private JPanel createFourEyesPanel(ResourceDialog owner, Set<YAtomicTask> preTasks) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(7, 5, 30, 5));
        chkFourEyes = createCheckBox("Do not choose completer(s) of task:", owner);
        cbxFourEyes = createCombo(owner, preTasks);
        panel.add(chkFourEyes, BorderLayout.CENTER);
        panel.add(cbxFourEyes, BorderLayout.EAST);
        panel.setSize(410, 25);
        return panel;
    }


    private JComboBox createCombo(ResourceDialog owner, Set<YAtomicTask> preTasks) {
        JComboBox combo = new JComboBox();
        combo.setPreferredSize(new Dimension(175, 25));
        combo.addActionListener(owner);
        addItems(combo, preTasks);
        combo.setEnabled(false);
        return combo;
    }


    private JCheckBox createCheckBox(String caption, ResourceDialog owner) {
        JCheckBox box = new JCheckBox(caption);
        box.addItemListener(this);
        box.addItemListener(owner);
        return box;
    }


    private void addItems(JComboBox combo, Set<YAtomicTask> preTasks) {
        java.util.List<String> items = new ArrayList<String>();
        for (YAtomicTask preTask : preTasks) {
            items.add(preTask.getID() + " (" + preTask.getNet().getID() + ")");
        }
        Collections.sort(items);
        for (String item : items) combo.addItem(item);
    }


    private boolean selectItem(JComboBox combo, String item) {
        for (int i=0; i < combo.getItemCount(); i++) {
            String taskID = StringUtil.firstWord((String) combo.getItemAt(i));
            if (taskID != null && taskID.equals(item)) {
                combo.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }


    private String getSelectedTaskID(JComboBox combo) {
        String selection = (String) combo.getSelectedItem();
        if (selection != null) {
            if (selection.contains(" (")) {
                selection = selection.substring(0, selection.indexOf(" ("));
            }
            return selection;
        }
        return null;
    }


    private void disableIfNoPresetTasks() {
        if (cbxFamTask.getItemCount() == 0) {
            chkFamTask.setEnabled(false);
            cbxFamTask.setEnabled(false);
            chkFourEyes.setEnabled(false);
            cbxFourEyes.setEnabled(false);
        }
    }


    private void showClashWarning(String taskName) {
        showWarningDialog("Task Selection Clash",
            "Task '" + taskName + "' has been selected for both constraint options.\n" +
            "This will result in an empty distribution set at runtime, since\n" +
            "the constraints are mutually exclusive. If both constraints\n" +
            "are required, please ensure each refers to a different task.");
    }


    private void showMissingTaskWarning(String constraint, String taskName) {
        showWarningDialog("Invalid Task Selection",
            "The current specification has task '" + taskName + "' selected as\n" +
            "the parameter for a " + constraint + " constraint, but there is\n" +
            "no task of that name preceding the current task. The constraint has\n" +
            "been deselected.");
    }


    private void showWarningDialog(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.WARNING_MESSAGE);
    }

}