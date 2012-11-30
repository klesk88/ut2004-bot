/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

/**
 *
 * @author Ado
 */
public interface Action 
{
    //pre - post conditionss
    
    /**
     * check if the pre-conditions are met. 
     * This can be done procedurally or simply by asking the worldstate about the truth-value of a symbol
     * @return true if all pre-conditions are met, false otherwise.
     */
    public boolean arePreConditionsMet();
   
    /**
     * Apply post-conditions to the (copy of the) world state
     */
    public void ApplyPostCondtions(WorldState.TruthStates[] worls_state);
    
    //public void UndoPostConditions();
    
    /**
     * update that check the state of the current action
     */
    public void update();
    
    
}
