/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.ActionManager;
import Actions.Action;
import MTC.MTC;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Michele,Tilman
 */
public class GOAPPlanner {
    
    private BlackBoard bb;
    private Stack<Action> finalPlan;
    private WorldState world_state;
    private final List<Action> actions;
    
    public GOAPPlanner()
    {
        bb = BlackBoard.getInstance();
        
        //get all the actions available for the bot
        actions = ActionManager.getInstance().getActionsAvailable();
        
        // get world state
        world_state = WorldState.getInstance();
        
    }
    
    public void replan()
    {
       
       
    // apply action
    // check whether goal is achieved
    // if not, apply other action or go back        
        List<Action> final_plan;
        
        BotLogic.getInstance().writeToLog_HackCosIMNoob("inside mtc");
        final_plan = MTC.getInstance().MTC(world_state.getWorldState(), world_state.getActualGoal());
        BotLogic.getInstance().writeToLog_HackCosIMNoob("outside mtc");
        for(int i=final_plan.size()-1; i>0; i--)
        {
            finalPlan.push(final_plan.get(i));
        }
        //write planb to blackboard
        bb.currentPlan = finalPlan;
        
    }
}
