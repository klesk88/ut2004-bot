/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

/**
 * @author Tilman, Michele
 */
public class WorldState {

    private static WorldState instance = null;

    public enum Symbols {
        PlayerIsVisible, HasSuppressionAmmunition, IsTargetDead, ShockGunAmmunition, HasLowHealth, 
    }

    public enum TruthStates {
        Uninstantiated, True, False
    }

    public enum GoalStates {
        KillEnemy, SearchRandomly, EmptyAmmunition, Survive
    }
    private boolean[] fixedSizeArray;
    private TruthStates[] goal_current;

    private WorldState() {
        goal_current = new TruthStates[]{TruthStates.Uninstantiated, TruthStates.Uninstantiated, TruthStates.True,TruthStates.Uninstantiated, TruthStates.Uninstantiated};
        setGoalState(GoalStates.KillEnemy);
        
        fixedSizeArray = new boolean[Symbols.values().length];
        for (int i = 0; i < fixedSizeArray.length; i++) {
            fixedSizeArray[i] = false;
        }
    }

    public static WorldState getInstance() {
        if (instance == null) {
            instance = new WorldState();
        }
        return instance;
    }

    /**
     * Change the real world state in one location
     * @param worldStateSymbol the symbol you want to change
     * @param value whether it is true or false in the real world
     */
    public void setWSValue(Symbols worldStateSymbol, boolean value) {
        fixedSizeArray[worldStateSymbol.ordinal()] = value;
    }

    /**
     * @return Whether the real world goal is achieved in the real world
     */
    public boolean IsGoalAchievedInRealWorld() {

        boolean value_to_return = true;

        for (int i = 0; i < goal_current.length; i++) {
            if (!(goal_current[i] == TruthStates.Uninstantiated)) {
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

    /**
     * @param goal: Select a pre-defined goal as real world goal
     */
    public void setGoalState(GoalStates goal) {
        for (int i = 0; i < goal_current.length; i++) {
            goal_current[i] = TruthStates.Uninstantiated;
        }

        switch (goal) {
            case KillEnemy:
                goal_current[Symbols.IsTargetDead.ordinal()] = TruthStates.True;
                break;
            case SearchRandomly:
                goal_current[Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;
                break;
            case Survive:
                goal_current[Symbols.HasLowHealth.ordinal()] = TruthStates.False;
                break;
            case EmptyAmmunition:
                // A goal for fun!
                goal_current[Symbols.IsTargetDead.ordinal()] = TruthStates.False;
                goal_current[Symbols.HasSuppressionAmmunition.ordinal()] = TruthStates.False;
                goal_current[Symbols.ShockGunAmmunition.ordinal()] = TruthStates.False;
                break;
                
        }
    }

    /**
     * @return the current real world goal
     */
    public TruthStates[] getActualGoal() {
        
        TruthStates[] temp = new TruthStates[goal_current.length];
        for(int i=0; i<goal_current.length;i++)
        {
            temp[i] = goal_current[i];
        }
        return temp;
    }

    /**
     * @return TruthStates[] containing a deep copy of the real world state
     */
    public WorldState.TruthStates[] getWorldState() {
        WorldState.TruthStates[] world_state = new TruthStates[fixedSizeArray.length];

        for (int i = 0; i < fixedSizeArray.length; i++) {
            if (fixedSizeArray[i]) {
                world_state[i] = TruthStates.True;
            } else {
                world_state[i] = TruthStates.False;
            }
        }
        //TODO: get copy
        return world_state;
    }

    /**
     * For simulation! Does not change real world state
     * @param currentWorldState the world state you want to use for simulation
     * @param postConditionizedFixedArray RECEIVE THIS FROM THE ACTION YOU WANT TO SIMULATE
     * @return a new simulated world state
     */
    public TruthStates[] applyPostConditionOfAction(TruthStates[] currentWorldState, TruthStates[] postConditionizedFixedArray) {
        TruthStates[] temp = new TruthStates[currentWorldState.length];
        
        for(int i=0; i<currentWorldState.length;i++)
        {
            temp[i] = currentWorldState[i];
        }
        
        for (int i = 0; i < currentWorldState.length; i++) {
            if (postConditionizedFixedArray[i] != TruthStates.Uninstantiated) {
                 temp[i] = postConditionizedFixedArray[i];
            }
        }
        return  temp;
    }

    /**
     * For Simulation! Does not change real world state
     * @param currentGoal the goal you want to start from
     * @param preConditionizedFixedArray RECEIVE THIS FROM THE ACTION YOU WANT TO SIMULATE
     * @return a new goal state including 
     *         which preconditions would have to be met to apply the action that gave the preConditionizedFixedArray
     */
    public TruthStates[] applyPreConditionOfAction(TruthStates[] currentGoal, TruthStates[] preConditionizedFixedArray) {
       
        TruthStates[] copy_cg = new TruthStates[currentGoal.length];
        
        for(int i=0; i<currentGoal.length;i++)
        {
            copy_cg[i]=currentGoal[i];
        }
        
        for (int i = 0; i < copy_cg.length; i++) {
            if (preConditionizedFixedArray[i] != TruthStates.Uninstantiated) {
                copy_cg[i] = preConditionizedFixedArray[i];
            }
        }
        return copy_cg;
    }

    /**
     * For simulation! Does not change real world state
     * @param worldState 
     * @param goalState
     * @return whether in the passed simulated world state the passed simulated goal would have been achieved
     */
    public boolean IsWorldStateAGoal(TruthStates[] worldState, TruthStates[] goalState) {
        boolean value_to_return = true;

        for (int i = 0; i < goalState.length; i++) {
            if (!(worldState[i] == TruthStates.Uninstantiated)) {
                if (goalState[i] == TruthStates.True  ) {
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
