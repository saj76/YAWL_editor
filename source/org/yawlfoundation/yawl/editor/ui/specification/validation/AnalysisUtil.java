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

import org.apache.logging.log4j.LogManager;
import org.yawlfoundation.yawl.analyser.YAnalyser;
import org.yawlfoundation.yawl.analyser.YAnalyserOptions;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.AnalysisDialog;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.FileLocations;
import org.yawlfoundation.yawl.editor.ui.util.UserSettings;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.util.JDOMUtil;

import java.io.File;

/**
 * @author Michael Adams
 * @date 30/07/2014
 */
public class AnalysisUtil {

    private static final String WOF_YAWL_BINARY = "wofyawl0.4.exe";


    public static String analyse(YAnalyser analyser, AnalysisDialog messageDlg,
                                 String specXML) {

        analyser.addEventListener(messageDlg);
        String _result;

        try {
            System.out.println("analyse");
            _result = analyser.analyse(specXML, getAnalyserOptions(),
                    UserSettings.getAnalyserMaxMarkings());
        } catch (YSyntaxException yse) {
            String msg = yse.getMessage().trim();
            msg = msg.substring(0, msg.indexOf(":")) + "." +
                    "\nAnalysis cannot proceed until these issues are resolved.\n" +
                    "Please validate the specification for more detailed information.";
            showError(messageDlg, msg);
            _result = "<error>Analysis aborted.</error>";
        } catch (IllegalArgumentException iae) {
            String msg = "\nNo analysis options selected. " +
                    "Please select at least one option\n" +
                    "in the analysis preferences list [File->Preferences->Analysis].";
            showError(messageDlg, msg);
            _result = "<error>Analysis aborted.</error>";
        } catch (Exception e) {
            messageDlg.setVisible(false);
            messageDlg.dispose();
            LogManager.getLogger(AnalysisUtil.class).error("Error analysing specification.", e);
            _result = "<error>" + JDOMUtil.encodeEscapes(e.getMessage()) + "</error>";
        } finally {
            if (messageDlg != null) {
                messageDlg.finished();
            }
            analyser.removeEventListener(messageDlg);
        }
        return _result;
    }

    public static String alloyAnalyse(YAnalyser analyser, AnalysisDialog messageDlg,
                                      String specXML) {

        analyser.addEventListener(messageDlg);
        String _alloyAnalysisResult;

        try {
            System.out.println("alloy analyse");
            _alloyAnalysisResult = analyser.alloyAnalyse(specXML, getAnalyserOptions(),
                    UserSettings.getAnalyserMaxMarkings());
        } catch (YSyntaxException yse) {
            String msg = yse.getMessage().trim();
            msg = msg.substring(0, msg.indexOf(":")) + "." +
                    "\nAnalysis cannot proceed until these issues are resolved.\n" +
                    "Please validate the specification for more detailed information.";
            showError(messageDlg, msg);
            _alloyAnalysisResult = "<error>Analysis aborted.</error>";
        } catch (IllegalArgumentException iae) {
            String msg = "\nNo analysis options selected. " +
                    "Please select at least one option\n" +
                    "in the analysis preferences list [File->Preferences->Analysis].";
            showError(messageDlg, msg);
            _alloyAnalysisResult = "<error>Analysis aborted.</error>";
        } catch (Exception e) {
            messageDlg.setVisible(false);
            messageDlg.dispose();
            LogManager.getLogger(AnalysisUtil.class).error("Error analysing specification.", e);
            _alloyAnalysisResult = "<error>" + JDOMUtil.encodeEscapes(e.getMessage()) + "</error>";
        } finally {
            if (messageDlg != null) {
                messageDlg.finished();
            }
            analyser.removeEventListener(messageDlg);
        }
        return _alloyAnalysisResult;
    }


    public static YAnalyserOptions getAnalyserOptions() {
        YAnalyserOptions options = new YAnalyserOptions();
        if (UserSettings.getResetNetAnalysis()) {
            options.enableResetWeakSoundness(UserSettings.getWeakSoundnessAnalysis());
            options.enableResetSoundness(UserSettings.getSoundnessAnalysis());
            options.enableResetCancellation(UserSettings.getCancellationAnalysis());
            options.enableResetOrJoin(UserSettings.getOrJoinAnalysis());
            options.enableResetOrjoinCycle(UserSettings.getOrJoinCycleAnalysis());
            options.enableResetReductionRules(UserSettings.getUseResetReductionRules());
            options.enableYawlReductionRules(UserSettings.getUseYawlReductionRules());
        }
        if (UserSettings.getAlloyAnalysis()) {
            options.enableAlloyOrJoinCycle(UserSettings.getAlloyOrJoinCycleAnalysis());
        }
        if (UserSettings.getWofyawlAnalysis()) {
            options.enableWofBehavioural(UserSettings.getBehaviouralAnalysis());
            options.enableWofStructural(UserSettings.getStructuralAnalysis());
            options.enableWofExtendedCoverabiity(UserSettings.getExtendedCoverability());
            options.setWofYawlExecutableLocation(getWofYawlExecutableFilePath());
        }
        return options;
    }


    public static String getWofYawlExecutableFilePath() {
        String path = UserSettings.getWofyawlFilePath();
        return path != null ? path : FileLocations.getHomeDir() + WOF_YAWL_BINARY;
    }


    public static AnalysisDialog createDialog(AnalysisCanceller canceller) {
        AnalysisDialog messageDlg = new AnalysisDialog("Specification",
                YAWLEditor.getInstance());
        messageDlg.setTitle("Analyse Specification");
        messageDlg.setOwner(canceller);
        return messageDlg;
    }


    public static boolean wofYawlAvailable() {
        String path = AnalysisUtil.getWofYawlExecutableFilePath();
        return path.endsWith(".exe") && new File(path).exists();
    }


    private static void showError(AnalysisDialog messageDlg, String msg) {
        messageDlg.setVisible(false);
        messageDlg.dispose();
        MessageDialog.error(msg, "Error analysing specification");
    }


}
