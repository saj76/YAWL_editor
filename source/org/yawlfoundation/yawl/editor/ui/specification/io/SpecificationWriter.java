/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.editor.ui.specification.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yawlfoundation.yawl.editor.core.YSpecificationHandler;
import org.yawlfoundation.yawl.editor.core.layout.YLayout;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.specification.validation.AnalysisResultsParser;
import org.yawlfoundation.yawl.editor.ui.specification.validation.DataTypeValidator;
import org.yawlfoundation.yawl.editor.ui.specification.validation.SpecificationValidator;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationResultsParser;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.UserSettings;
import org.yawlfoundation.yawl.elements.YSpecification;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SpecificationWriter extends SwingWorker<Boolean, Void> {

    private final YSpecificationHandler _handler = SpecificationModel.getHandler();
    private final Logger _log = LogManager.getLogger(this.getClass());

    private String _fileName;
    private boolean _successful;

    public SpecificationWriter() {
    }

    public SpecificationWriter(String fileName) {
        _fileName = fileName;
    }


    public Boolean doInBackground() {
        _successful = false;

        if (_fileName != null) {
            try {
                if (checkUserDefinedDataTypes()) {
                    YLayout layout = new LayoutExporter().parse();
                    cleanSpecification();
                    checkSpecification();
                    checkAlloySpecification();
                    if (!_fileName.equals(_handler.getFileName())) {
                        _handler.saveAs(_fileName, layout, UserSettings.getFileSaveOptions());
                        firePropertyChange("Uri", _handler.getURI());
                    } else {
                        _handler.save(layout, UserSettings.getFileSaveOptions());
                    }
                    firePropertyChange("Version", _handler.getVersion());
                    _successful = true;
                }
            } catch (Exception e) {
                new FileSaveErrorNotifier().notify(e, _fileName);
                _log.error("Error saving specification to file.", e);
            }
        }
        return _successful;
    }


    public boolean successful() {
        return _successful;
    }


    public String getSpecificationXML() {
        cleanSpecification();
        try {
            return _handler.getSpecificationXML();
        } catch (Exception e) {
            _log.error("Error marshalling specification to XML.", e);
            return null;
        }
    }


    public YSpecification cleanSpecification() {
        _handler.getControlFlowHandler().removeOrphanTaskDecompositions();
        _handler.getControlFlowHandler().removeIncompleteFlows();
        _handler.getUniqueID();  // build UUID if updating from Beta version schema
        _handler.getMetaData().setCoverage(YAWLEditor.BUILD_VERSION);
        return _handler.getSpecification();
    }


    /***************************************************************************/

    private boolean checkUserDefinedDataTypes() {
        List<String> results = new DataTypeValidator().validate();
        if (!results.isEmpty()) {
            YAWLEditor.getInstance().showProblemList("Export Errors",
                    new ValidationResultsParser().parse(results));
            MessageDialog.error(
                    "Could not export Specification due to missing or invalid user-" +
                            "defined data types.\nPlease see the problem list below for details.",
                    "Data type Error");
        }
        return results.isEmpty();
    }


    private void checkSpecification() {
        List<String> results = new ArrayList<String>();
        String title = "Validation";
        if (UserSettings.getVerifyOnSave()) {
            results.addAll(new SpecificationValidator().getValidationResults(
                    _handler.getSpecification()));
        }
        if (UserSettings.getAnalyseOnSave()) {
            title = "Analysis";
            try {
                System.out.println("specification writer");
                AnalysisResultsParser parser = new AnalysisResultsParser();
                results.addAll(parser.getAnalysisResults(_handler.getSpecificationXML()));
            } catch (Exception e) {
                // analysis failed
            }
        }
        YAWLEditor.getInstance().showProblemList(title + " Results",
                new ValidationResultsParser().parse(results));
    }

    private void checkAlloySpecification() {
        List<String> results = new ArrayList<String>();
        String title = "Validation";
        if (UserSettings.getAnalyseOnSave()) {
            title = "Alloy Analysis";
            try {
                System.out.println("alloy specification writer");
                AnalysisResultsParser parser = new AnalysisResultsParser();
                results.addAll(parser.getAlloyAnalysisResults(_handler.getSpecificationXML()));
            } catch (Exception e) {
                // analysis failed
            }
        }
        YAWLEditor.getInstance().showAlloyProblemList(title + " Results",
                new ValidationResultsParser().parse(results));
    }

    private void firePropertyChange(String property, Object newValue) {
        YAWLEditor.getPropertySheet().firePropertyChange(property, newValue);
    }

}
