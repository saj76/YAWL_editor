package org.yawlfoundation.yawl.editor.resourcing;

import org.yawlfoundation.yawl.editor.data.DataVariable;
import org.yawlfoundation.yawl.editor.data.DataVariableUtilities;
import org.yawlfoundation.yawl.editor.data.Decomposition;
import org.yawlfoundation.yawl.editor.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.net.NetGraph;
import org.yawlfoundation.yawl.editor.swing.YAWLEditorDesktop;

import java.io.Serializable;
import java.util.*;

public class ResourceMapping implements Serializable, Cloneable  {
  
  private static final long serialVersionUID = 1L;
  
  public static final int SYSTEM_INTERACTION_POINT = 0;
  public static final int USER_INTERACTION_POINT = 1;

  public static final int CAN_SUSPEND_PRIVILEGE              = 100;
  public static final int CAN_REALLOCATE_STATELESS_PRIVILEGE = 101;
  public static final int CAN_REALLOCATE_STATEFUL_PRIVILEGE  = 102;
  public static final int CAN_DEALLOCATE_PRIVILEGE           = 103;
  public static final int CAN_DELEGATE_PRIVILEGE             = 104;
  public static final int CAN_SKIP_PRIVILEGE                 = 105;
  public static final int CAN_PILE_PRIVILEGE                 = 106;

  /* ALL yawl-specific attributes of this object and its descendants 
   * are to be stored in serializationProofAttributeMap, meaning we 
   * won't get problems with incompatible XML serializations as we add 
   * new attributes in the future. 
   */
  
  protected HashMap serializationProofAttributeMap = new HashMap();

  public ResourceMapping() {
    super();
    initialise();
  }

  private void initialise() {
    setOfferInteractionPoint(USER_INTERACTION_POINT);
    setAllocateInteractionPoint(USER_INTERACTION_POINT);
    setStartInteractionPoint(USER_INTERACTION_POINT);
    setEnabledPrivileges(new HashSet<Integer>());
  }
  
  public void setSerializationProofAttributeMap(HashMap map) {
    this.serializationProofAttributeMap = map;
  }
  
  public HashMap getSerializationProofAttributeMap() {
    return this.serializationProofAttributeMap;
  }
  
  public ResourceMapping(YAWLAtomicTask resourceRequiringTask) {
    super();
    initialise();
    setResourceRequiringTask(
        resourceRequiringTask
    );
    setBaseVariableContentList(
        buildDefaultBaseVariableContentList()
    );
  }
  
  public YAWLAtomicTask getResourceRequiringTask() {
    return (YAWLAtomicTask) serializationProofAttributeMap.get("resourceRequiringTask");
  }
  
  public void setResourceRequiringTask(YAWLAtomicTask resourceRequiringTask) {
    serializationProofAttributeMap.put("resourceRequiringTask",resourceRequiringTask);
  }

  /* ------ Offer Related Attributes ------ */
  
  public void setOfferInteractionPoint(int setting) {
    serializationProofAttributeMap.put("offerInteractionPoint",new Integer(setting));
  }
  
  public int getOfferInteractionPoint() {
    return ((Integer) serializationProofAttributeMap.get("offerInteractionPoint")).intValue();
  }
  
  public YAWLAtomicTask getRetainFamiliarTask() {
    return (YAWLAtomicTask) serializationProofAttributeMap.get("retainFamiliarTask");
  }

  public void setRetainFamiliarTask(YAWLAtomicTask task) {
    serializationProofAttributeMap.put("retainFamiliarTask", task);
  }

  
  public YAWLAtomicTask getSeparationOfDutiesTask() {
    return (YAWLAtomicTask) serializationProofAttributeMap.get("separationOfDutiesTask");
  }

  public void setSeparationOfDutiesTask(YAWLAtomicTask task) {
    serializationProofAttributeMap.put("separationOfDutiesTask", task);
  }
  
  private List<DataVariableContent> buildDefaultBaseVariableContentList() {
    LinkedList<DataVariableContent> list = new LinkedList<DataVariableContent>();

    List<DataVariable> validPossibleVariables = getNetVariablesValidForResourcing();
    for(DataVariable variable : validPossibleVariables) {
      
      list.add(new DataVariableContent(variable));
    }

    return list;
  }


  private List<DataVariable> getNetVariablesValidForResourcing() {
    NetGraph selectedGraph = YAWLEditorDesktop.getInstance().getSelectedGraph() ;
    Decomposition decomp = selectedGraph.getNetModel().getDecomposition();

    return  DataVariableUtilities.getVariablesOfType(decomp.getVariables(),
                                         DataVariable.XML_SCHEMA_STRING_TYPE);
  }


  public void setBaseUserDistributionList(List<ResourcingParticipant> userList) {
    serializationProofAttributeMap.put("baseUserDistributionList", userList);
  }
  
  public List<ResourcingParticipant> getBaseUserDistributionList() {
    return (List<ResourcingParticipant>) serializationProofAttributeMap.get("baseUserDistributionList");
  }

  public void setBaseRoleDistributionList(List<ResourcingRole> roles) {
    serializationProofAttributeMap.put("baseRoleDistributionList", roles);
  }
  
  public List<ResourcingRole> getBaseRoleDistributionList() {
    return (List<ResourcingRole>) serializationProofAttributeMap.get("baseRoleDistributionList");
  }
  
  public void setBaseVariableContentList(List<DataVariableContent> list) {
    serializationProofAttributeMap.put("baseVariableContentList", list);
  }
  
  public List<DataVariableContent>  getBaseVariableContentList() {
    return (List<DataVariableContent>) serializationProofAttributeMap.get("baseVariableContentList");
  }

  /**
   * Resynchronises the variable content map with changes that may have been
   * applied to the task's data perspective definitions. All new variables that
   * could be used to store resourcing data default to storing plain data initially.
   */
  
  public void syncWithDataPerspective() {
    if (getBaseVariableContentList() == null) {
      return;  // nothing to do if none have been specified.
    }
    
    LinkedList<DataVariableContent> variablesToRemove = new LinkedList<DataVariableContent>();
    for(DataVariableContent variableContent : getBaseVariableContentList()) {
      if (!variableContent.isValidForResourceContainment()) {
        variablesToRemove.add(variableContent);
      }
    }
    
    for(DataVariableContent variableContent : variablesToRemove) {
      getBaseVariableContentList().remove(variableContent);
    }
    
    LinkedList<DataVariable> variablesToAdd = new LinkedList<DataVariable>();
    List<DataVariable> varList = getNetVariablesValidForResourcing();
    for(DataVariable variable : varList) {
      boolean variableFound = false;
      for(DataVariableContent variableContent : getBaseVariableContentList()) {
        if (variableContent.getVariable() == variable) {
          variableFound = true;
          break;
        }
      }
      if (!variableFound && DataVariableContent.isValidForResourceContainment(variable)) {
        variablesToAdd.add(variable);
      }
    }
    
    for(DataVariable variable: variablesToAdd) {
      getBaseVariableContentList().add(
          new DataVariableContent(variable)
      );
    }
  }

  public void setResourcingFilters(List<ResourcingFilter> filters) {
    serializationProofAttributeMap.put("resourcingFilters", filters);
  }
  
  public List<ResourcingFilter>  getResourcingFilters() {
    return (List<ResourcingFilter>) serializationProofAttributeMap.get("resourcingFilters");
  }
  
  /* ------ Allocation Related Attributes ------ */
  
  public void setAllocateInteractionPoint(int setting) {
    serializationProofAttributeMap.put("allocateInteractionPoint",new Integer(setting));
    
    if (getAllocationMechanism() == null) {
      setAllocationMechanism(AllocationMechanism.DEFAULT_MECHANISM);
    }
  }
  
  public int getAllocateInteractionPoint() {
    return ((Integer) serializationProofAttributeMap.get("allocateInteractionPoint")).intValue();
  }

  public AllocationMechanism getAllocationMechanism() {
    if (getAllocateInteractionPoint() == SYSTEM_INTERACTION_POINT) {
      return (AllocationMechanism) serializationProofAttributeMap.get("allocationMechanism");
    }
    return null;
  }
  
  public void setAllocationMechanism(AllocationMechanism allocationMechanism) {
    serializationProofAttributeMap.put("allocationMechanism", allocationMechanism);
  }

  /* ------ Start Related Attributes ------ */
  
  public void setStartInteractionPoint(int setting) {
    serializationProofAttributeMap.put("startInteractionPoint",new Integer(setting));
  }

  
  public int getStartInteractionPoint() {
    return ((Integer) serializationProofAttributeMap.get("startInteractionPoint")).intValue();
  }

  
  /* ------ Privilege Related Attributes ------ */

  public void setEnabledPrivileges(HashSet<Integer> privileges) {
    serializationProofAttributeMap.put("enabledPrivileges", privileges);
  }
  
  public HashSet<Integer> getEnabledPrivileges() {
    return (HashSet<Integer>) serializationProofAttributeMap.get("enabledPrivileges");
  }
  
  public void enablePrivilege(int privilege, boolean enabled) {
    if (enabled) {
      getEnabledPrivileges().add(new Integer(privilege));
    } else {
      getEnabledPrivileges().remove(new Integer(privilege));
    }
  }
  
  public boolean isPrivilegeEnabled(int  privilege) {
    if (getEnabledPrivileges().contains(new Integer(privilege))) {
      return true;
    }
    return false;
  }
  
  public String toString() {
    
    StringBuffer systemAllocationMechanism = new StringBuffer("");
    
    if (getAllocateInteractionPoint() == SYSTEM_INTERACTION_POINT) {
      if (getAllocationMechanism() != null) {
        systemAllocationMechanism.append(
            "System Allocation Mechanism\n" + 
            "---------------------------\n" + 
            "  " + getAllocationMechanism().getDisplayName() + "\n"
        );
      }
    }
    
    StringBuffer baseUserDistribitionListString = new StringBuffer("");
    if (getBaseUserDistributionList() != null && getBaseUserDistributionList().size() > 0) {
      baseUserDistribitionListString.append(
          "Base User Distribution List:\n" + 
          "---------------------------\n"
      );
      for(ResourcingParticipant user : getBaseUserDistributionList()) {
        baseUserDistribitionListString.append("  " + user.getName() + "\n");
      }
    }

    StringBuffer baseRoleDistribitionListString = new StringBuffer("");
    if (getBaseRoleDistributionList() != null && getBaseRoleDistributionList().size() > 0) {
      baseRoleDistribitionListString.append(
          "Base RoleDistribution List:\n" + 
          "---------------------------\n"
      );
      for(ResourcingRole role : getBaseRoleDistributionList()) {
        baseRoleDistribitionListString.append("  " + role.getName() + "\n");
      }
    }

    StringBuffer baseVariableContentListString = new StringBuffer("");
    if (getBaseVariableContentList() != null && getBaseVariableContentList().size()> 0) {
      baseVariableContentListString.append(
          "Variable Content List:\n" + 
          "----------------------\n"
      );
      for(DataVariableContent content: getBaseVariableContentList()) {
        baseVariableContentListString.append(
            "  " + content.getVariable().getName() + 
            " contains " + 
            content.getContentTypeAsString() + "\n");
      }
    }

    StringBuffer resourceFiltersString = new StringBuffer("");
    if (getResourcingFilters() != null && getResourcingFilters().size()> 0) {
      resourceFiltersString.append(
          "Resource Filters:\n" + 
          "----------------------\n"
      );
      for(ResourcingFilter filter : getResourcingFilters()) {
        resourceFiltersString.append(
            "  " + filter.getDisplayName() +  "\n"
        );
      }
    }
    
    StringBuffer retainFamiliarTaskString = new StringBuffer("");
    if(getRetainFamiliarTask() != null) {
      retainFamiliarTaskString.append("Retain Familiar Task = (" 
          + ((YAWLTask) getRetainFamiliarTask()).getEngineId()
          + ").\n"
      );
    }

    StringBuffer separationOfDutiesTaskString = new StringBuffer("");
    if(getSeparationOfDutiesTask() != null) {
      retainFamiliarTaskString.append("Separation of Duties Task = (" 
          + ((YAWLTask) getSeparationOfDutiesTask()).getEngineId()
          + ").\n"
      );
    }

    StringBuffer enabledPrivilegesString = new StringBuffer("");
    
    if (getEnabledPrivileges().size() > 0) {
      enabledPrivilegesString.append(
          "Enabled Runtime Privileges:\n" + 
          "---------------------------\n"
      );
      for(Integer privilege: getEnabledPrivileges()) {
        enabledPrivilegesString.append("  " + privilege + "\n");
      }
    }
    
    return 
        "Interaction Points:\n-------------------\n  Offer: " + getOfferInteractionPoint() + 
        ", Allocate: " + getAllocateInteractionPoint() + 
        ", Start: " + getStartInteractionPoint() + "\n" +
        systemAllocationMechanism +
        retainFamiliarTaskString +
        baseUserDistribitionListString +
        baseRoleDistribitionListString +
        baseVariableContentListString +
        resourceFiltersString +
        separationOfDutiesTaskString +
        enabledPrivilegesString;
  }
}