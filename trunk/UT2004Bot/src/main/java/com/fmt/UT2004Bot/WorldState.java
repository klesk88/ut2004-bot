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

    private static WorldState instance = null;
    public enum Symbols{PlayerIsVisible, HasSuppressionAmmunition, IsTargetDead}  
    public enum TruthStates {Uninstantiated, True, False}
    public enum GoalStates {KillEnemy, SearchRandomly}
    
    private boolean[] fixedSizeArray;
    private boolean[] fixedSizeArray_save;
    
    //private boolean[] currentGoalState;
    
    private WorldState.TruthStates[] world_state;
    private TruthStates[] goal_current;
    
    // this is an awesome name for a hashmap!
    //Map<WorldStates,Boolean> fixedSizeArray = new HashMap<WorldStates,Boolean>();
    

     private WorldState() {
         init();
        // Exists only to defeat instantiation.
    }

    public static WorldState getInstance() {
        if (instance == null) {
            instance = new WorldState();
        }
        return instance;
    }
    
    public void init()
    {
        goal_current = new TruthStates[]{TruthStates.Uninstantiated, TruthStates.Uninstantiated, TruthStates.True};
        
        fixedSizeArray = new boolean[Symbols.values().length];
        //currentGoalState = new boolean[Symbols.values().length];
        for (int i = 0; i< fixedSizeArray.length; i++)
        {
            fixedSizeArray[i] = false;
          //  currentGoalState[i] = false;
        }
       //fixedSizeArray.put(WorldStates.AtTargetPos, Boolean.FALSE);
       //fixedSizeArray.put(WorldStates.ReloadWeapon, Boolean.FALSE);
    }
    
    public void setWSValue(Symbols worldStateSymbol, boolean value )
    {
        BotLogic.getInstance().writeToLog_HackCosIMNoob("set value");
        fixedSizeArray[worldStateSymbol.ordinal()] = value;
    }
    
    public boolean IsGoalAchieved()
    {
        // compare world state with goal_current
        
        boolean value_to_return = true;
        
        for (int i = 0; i < goal_current.length; i++)
        {
            if (!(goal_current[i] == TruthStates.Uninstantiated))
            {
                if (goal_current[i] == TruthStates.True) {
                    if (!fixedSizeArray[i]) {
                        value_to_return = false;
                    }
                } else if (goal_current[i] == TruthStates.False) {
                    if (fixedSizeArray[i]) {
                        value_to_return = false;
                    }
                }
            }     
        }

        return value_to_return;
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
    
    //return current world state
    public WorldState.TruthStates[] getWorldState()
    {
        world_state = new TruthStates[fixedSizeArray.length];
        
        for (int i = 0; i < fixedSizeArray.length; i++)
        {
            if (fixedSizeArray[i])
                world_state[i] = TruthStates.True;
            else
                world_state[i] = TruthStates.False;
        }
        //TODO: get copy
        return world_state;
    }
    
    //goal world state
    public TruthStates[] getActualGoal()
    {
        return goal_current;
    }
    
        public TruthStates[] applyPostConditionOfAction(TruthStates[] currentWorldState, TruthStates[] postConditionizedFixedArray)
    {
        for(int i =0; i<currentWorldState.length; i++)
        {
            if(postConditionizedFixedArray[i]!= TruthStates.Uninstantiated)
            {
                currentWorldState[i] = postConditionizedFixedArray[i];
            }
        }
        return currentWorldState;
    }
    
    public TruthStates[] applyPreConditionOfAction(TruthStates[] currentGoal, TruthStates[] preConditionizedFixedArray)
    {
        for(int i =0; i<currentGoal.length; i++)
        {
            if(preConditionizedFixedArray[i]!= TruthStates.Uninstantiated)
            {
                currentGoal[i] = preConditionizedFixedArray[i];
            }
        }
        return currentGoal;
    }

    public boolean IsWorldStateAGoal(TruthStates[] worldState, TruthStates[] goalState)
    {
        boolean value_to_return = true;
        
        for (int i = 0; i < goalState.length; i++)
        {
            if (!(goalState[i] == TruthStates.Uninstantiated))
            {
                if (goalState[i] == TruthStates.True) {
                    if (worldState[i] == TruthStates.False) {
                        value_to_return = false;
                    }
                } else if (goalState[i] == TruthStates.False) {
                    if (worldState[i] == TruthStates.True) {
                        value_to_return = false;
                    }
                }
            }     
        }

        return value_to_return;
    }
}

