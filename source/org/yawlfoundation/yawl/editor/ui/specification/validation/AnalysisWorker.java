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

package org.yawlfoundation.yawl.editor.ui.specification.validation;

import org.apache.jena.base.Sys;
import org.yawlfoundation.yawl.analyser.YAnalyser;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationWriter;
import org.yawlfoundation.yawl.editor.ui.swing.AnalysisDialog;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.swing.*;

/**
 * @author Michael Adams
 * @date 30/07/2014
 */
public class AnalysisWorker extends SwingWorker<Void, Void> implements AnalysisCanceller {

    private String _result;
    private String _alloyResult;
    private YAnalyser _analyser;


    protected AnalysisWorker() {
    }


    protected String getResult() {
        return _result;
    }

    protected String getAlloyValidationResult() {
        return _alloyResult;
    }


    public void cancel() {
        if (_analyser != null) {
            _analyser.cancelAnalysis();
            _result = StringUtil.wrap("Analysis cancelled.", "cancelled");
        }
        cancel(true);
    }


    @Override
    protected Void doInBackground() throws Exception {
        String specXML = new SpecificationWriter().getSpecificationXML();
        AnalysisDialog messageDlg = AnalysisUtil.createDialog(this);
        _analyser = new YAnalyser();
        System.out.println("background");
        _result = AnalysisUtil.analyse(_analyser, messageDlg, specXML);
        _alloyResult = AnalysisUtil.alloyAnalyse(_analyser, messageDlg, specXML);
        return null;
    }


    protected void done() {
        try {
            if (!isCancelled()) {
                get();
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = e.getClass().getName();
            }
            MessageDialog.error(msg, "Unexpected Error");
        }
    }

}
