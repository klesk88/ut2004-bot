/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.*;
import MTC.MTC;
import java.util.Stack;

/**
 * This class should manage ALL actions the bot takes.
 *
 * Classes e.g. for moving the bot should be managed by this class (e.g.
 * deciding which movement module gets )
 *
 * @author Tilman
 */
public class ActorSystem {

    //SET TO TRUE IF YOU WANT TO USE PLANNING
    boolean PLANNING_ENABLED = true;
    BlackBoard bb;
    
    private GOAPPlanner planner = GOAPPlanner.getInstance();
    private static ActorSystem instance;

    public static ActorSystem getInstance() {
        if (instance == null) {
            instance = new ActorSystem();
        }
        return instance;
    }

    private ActorSystem() {
        BotLogic.getInstance().writeToLog_HackCosIMNoob("init start actorSystem");

        bb = BlackBoard.getInstance();
        
        if (PLANNING_ENABLED) {
            MTC.getInstance().init(0.3f, 10, 20);
            planner.replan();
        } else {
            bb.currentPlan = new Stack<Actions.Action>();
            //testStack();
            BotLogic.getInstance().writeToLog_HackCosIMNoob(" !!! PLANNING DISABLED !!!");
        }
    }

    /**
     * Write your test stacks here. Will only be called if PLANNER_ENABLED =
     * false;
     */
    private void testStack() {

        //bb.currentPlan.push(action_randomWalk);
        //bb.currentPlan.push(gt);
    }

    /**
     * Will execute actions from the current plan and ask for new plans upon
     * failure or plan completion.
     */
    public void update() {

        if (bb.currentPlan.isEmpty() ) {
            if (PLANNING_ENABLED) {
                planner.replan();
            } else {
                //testStack();
            }
        }
        
        //when there are  actions taht satisfied the goal
        if(bb.currentPlan.size() != 0)
        {
                //if running we will not deal with the result and just run
            Actions.Action.ActionResult result = bb.currentPlan.peek().executeAction();

            if (result == Actions.Action.ActionResult.Success) {
                bb.currentPlan.pop();
            }
            if (result == Actions.Action.ActionResult.Failed) {
                bb.currentPlan.pop();
                if (PLANNING_ENABLED) {
                    planner.replan();
                }
            }
        }
        
      
    }
}
