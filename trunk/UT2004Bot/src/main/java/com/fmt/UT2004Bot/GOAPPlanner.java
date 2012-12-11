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
    
    private static GOAPPlanner instance;
    
    private GOAPPlanner()
    {
        bb = BlackBoard.getInstance();
        
        //get all the actions available for the bot
        actions = ActionManager.getInstance().getActionsAvailable();
        
        // get world state
        world_state = WorldState.getInstance();
        finalPlan = new Stack<Action>();
        
    }
    
    

    public static GOAPPlanner getInstance() {
        if (instance == null) {
            instance = new GOAPPlanner();
        }
        return instance;
    }
    //SINGLETON END
    
    public void replan()
    {
       
       
    // apply action
    // check whether goal is achieved
    // if not, apply other action or go back        
        List<Action> final_plan;
        finalPlan.clear();
       
        final_plan = MTC.getInstance().MTC(world_state.getWorldState(), world_state.getActualGoal());
      
        for(int i=0; i<final_plan.size(); i++)
        {
            finalPlan.push(final_plan.get(i));
        }
        //write planb to blackboard
        bb.currentPlan = finalPlan;
         BotLogic.getInstance().writeToLog_HackCosIMNoob("final plan: ");
         for(int i=0; i<finalPlan.size();i++)
         {
             BotLogic.getInstance().writeToLog_HackCosIMNoob(finalPlan.get(i).getClass().getName());
         }
    }
}
