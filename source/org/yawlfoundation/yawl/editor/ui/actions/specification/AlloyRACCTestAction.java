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

package org.yawlfoundation.yawl.editor.ui.actions.specification;

import org.yawlfoundation.yawl.editor.ui.specification.FileOperations;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AlloyRACCTestAction extends YAWLOpenSpecificationAction
        implements TooltipTogglingWidget {

  {
    putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
    putValue(Action.NAME, "Test by Alloy");
    putValue(Action.SMALL_ICON, getMenuIcon("alloy_test"));
    putValue(Action.LONG_DESCRIPTION, "Test by Alloy based on RACC coverage criteria.");
  }
  
  public void actionPerformed(ActionEvent event) {
      FileOperations.testWithAlloy_RACC();
  }
  
  public String getEnabledTooltipText() {
    return " Test by Alloy based on RACC coverage criteria";
  }
  
  public String getDisabledTooltipText() {
    return " You must have an open specification" + 
           " to test it by Alloy ";
  }
}
