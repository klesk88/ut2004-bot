/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import Actions.Action_SuppressionFire;
import Actions.Action_RandomWalk;
import Actions.Action_FollowVisiblePlayer;
import Actions.Action;
import Actions.Action_GoToAmmunition;
import MTC.MTC;
import java.util.Stack;

/**
 * This class should manage ALL actions the bot takes. 
 * 
 * Classes e.g. for moving the bot should be managed by this class (e.g. deciding which movement module gets )
 * @author Tilman
 */
public class ActorSystem {
    
    BlackBoard bb;
    
    Action_SuppressionFire action_suppressionFire;
    Action_GoToAmmunition action_gotoammunition;
    Action_FollowVisiblePlayer action_followVisiblePlayer;
    Action_RandomWalk action_randomWalk;
    private GOAPPlanner planner;
    private static ActorSystem instance;
    
    public static ActorSystem getInstance() {
        if (instance == null) {
            instance = new ActorSystem();
        }
        return instance;
    }
    
    private ActorSystem()
    {
        bb = BlackBoard.getInstance();
        action_suppressionFire = new Action_SuppressionFire(); 
        action_gotoammunition = new Action_GoToAmmunition();
        action_followVisiblePlayer = new Action_FollowVisiblePlayer();
        action_randomWalk = new Action_RandomWalk();
          MTC.getInstance().init(0.05f , 3,20);
        planner = new GOAPPlanner();
        planner.replan();
        
    }
    
    public void update()
    {

            if(bb.currentPlan.isEmpty())
            {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("34254325");
            planner.replan();
            BotLogic.getInstance().writeToLog_HackCosIMNoob("8986786");
        }
        Action.ActionResult result = bb.currentPlan.firstElement().executeAction();
        
        if (result == Action.ActionResult.Success)
            bb.currentPlan.pop();
        if (result == Action.ActionResult.Failed)
            planner.replan();;
        
      
        
        /*
        if (action_followVisiblePlayer.executeAction() == Action.ActionResult.Running)
        {
            action_suppressionFire.executeAction();
        }
        else if ( !(action_gotoammunition.executeAction() == Action.ActionResult.Success))
        {
            action_randomWalk.executeAction();
        }
           
        */
        //
        
        //if (!bb.follow_player) action_gotoammunition.executeAction();
        
        //ask current action about its status
        //if action terminated ask blackboard which action is next in the goap plan
        
        //low level action like navigation
     
        //if(bb.currentPlan.IsEmpty) 
        //  bb.replan = true;
        
        
        
    }
    
}
