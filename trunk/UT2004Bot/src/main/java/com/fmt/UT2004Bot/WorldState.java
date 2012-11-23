/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

/**
 *
 * @author Tilman, Michele
 */
public class WorldState {

    public enum Symbols{AtTargetPos, IsWeaponLoaded, IsTargetDead}  
    public enum TruthStates {Uninstantiated, True, False}
    public enum GoalStates {KillEnemy, SearchRandomly}
    
    private boolean[] fixedSizeArray;
    private boolean[] fixedSizeArray_save;
    
    
    private boolean[] currentGoalState;
    
    private TruthStates[] goal_current;
    
    // this is an awesome name for a hashmap!
    //Map<WorldStates,Boolean> fixedSizeArray = new HashMap<WorldStates,Boolean>();
    

    
    public WorldState()
    {
        goal_current = new TruthStates[]{TruthStates.Uninstantiated, TruthStates.Uninstantiated, TruthStates.True};
        
        fixedSizeArray = new boolean[Symbols.values().length];
        currentGoalState = new boolean[Symbols.values().length];
        for (int i = 0; i< fixedSizeArray.length; i++)
        {
            fixedSizeArray[i] = false;
            currentGoalState[i] = false;
        }
       //fixedSizeArray.put(WorldStates.AtTargetPos, Boolean.FALSE);
       //fixedSizeArray.put(WorldStates.ReloadWeapon, Boolean.FALSE);
    }
    
    public void setWSValue(Symbols worldStateSymbol, boolean value )
    {
        fixedSizeArray[worldStateSymbol.ordinal()] = value;
    }
    
    public boolean IsGoalAchieved()
    {
        // compare world state with goal_current
        
        return false;
    }
    
    public void setGoalState(GoalStates goal)
    {
        for (int i = 0; i < goal_current.length; i++)
        {
            goal_current[i] = TruthStates.Uninstantiated;
        }
        
        switch(goal)
        {
            case KillEnemy:
                goal_current[Symbols.IsTargetDead.ordinal()] = TruthStates.True;
        }
    }
    
    //key value pairs
        // for example: is target dead, is weapon loaded (these two are only one slot, so a differet class needs to manage these)
        // others: at target pos, 
    
    
    
    //current world state
    
    //goal world state
    
}

