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
        PlayerIsVisible, HasSuppressionAmmunition, IsTargetDead
    }

    public enum TruthStates {
        Uninstantiated, True, False
    }

    public enum GoalStates {
        KillEnemy, SearchRandomly
    }
    private boolean[] fixedSizeArray;
    private TruthStates[] goal_current;

    private WorldState() {
        goal_current = new TruthStates[]{TruthStates.Uninstantiated, TruthStates.Uninstantiated, TruthStates.True};
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
        }
    }

    /**
     * @return the current real world goal
     */
    public TruthStates[] getActualGoal() {
        return goal_current;
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
        for (int i = 0; i < currentWorldState.length; i++) {
            if (postConditionizedFixedArray[i] != TruthStates.Uninstantiated) {
                currentWorldState[i] = postConditionizedFixedArray[i];
            }
        }
        return currentWorldState;
    }

    /**
     * For Simulation! Does not change real world state
     * @param currentGoal the goal you want to start from
     * @param preConditionizedFixedArray RECEIVE THIS FROM THE ACTION YOU WANT TO SIMULATE
     * @return a new goal state including 
     *         which preconditions would have to be met to apply the action that gave the preConditionizedFixedArray
     */
    public TruthStates[] applyPreConditionOfAction(TruthStates[] currentGoal, TruthStates[] preConditionizedFixedArray) {
        for (int i = 0; i < currentGoal.length; i++) {
            if (preConditionizedFixedArray[i] != TruthStates.Uninstantiated) {
                currentGoal[i] = preConditionizedFixedArray[i];
            }
        }
        return currentGoal;
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
            if (!(goalState[i] == TruthStates.Uninstantiated)) {
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
