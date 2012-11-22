

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import java.util.Map;
import java.util.HashMap;


/**
 *
 * @author Tilman, Michele
 */
public class WorldState {

    public enum Symbols{AtTargetPos, ReloadWeapon, }
    
    private boolean[] fixedSizeArray;
    
    private boolean[] currentGoalState;
    
    // this is an awesome name for a hashmap!
    //Map<WorldStates,Boolean> fixedSizeArray = new HashMap<WorldStates,Boolean>();
    
    public WorldState()
    {
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
    
    //key value pairs
        // for example: is target dead, is weapon loaded (these two are only one slot, so a differet class needs to manage these)
        // others: at target pos, 
    
    
    
    //current world state
    
    //goal world state
    
}

