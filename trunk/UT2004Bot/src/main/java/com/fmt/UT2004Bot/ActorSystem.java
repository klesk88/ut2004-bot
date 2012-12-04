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

    BlackBoard bb;
    Actions.Action_SuppressionFire action_suppressionFire;
    Actions.Action_GoToAmmunition action_gotoammunition;
    Actions.Action_FollowVisiblePlayer action_followVisiblePlayer;
    Actions.Action_RandomWalk action_randomWalk;
    Actions.Action_ShockGunNuke action_schock;
    Actions.Action_FindShockGunAmmo action_schockAmmoFinding;
    private GOAPPlanner planner;
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
        action_suppressionFire = new Actions.Action_SuppressionFire();
        action_gotoammunition = new Actions.Action_GoToAmmunition();
        action_followVisiblePlayer = new Actions.Action_FollowVisiblePlayer();
        action_randomWalk = new Actions.Action_RandomWalk();
        action_schock = new Actions.Action_ShockGunNuke();
        action_schockAmmoFinding = new Actions.Action_FindShockGunAmmo();

        //bb.currentPlan = new Stack<Actions.Action>();
        //bb.currentPlan.push(action_schock);
        //bb.currentPlan.push(action_randomWalk);
        //bb.currentPlan.push(action_schockAmmoFinding);




        BotLogic.getInstance().writeToLog_HackCosIMNoob("demoplan actorSystem");

        //MTC.getInstance().init(0.05f , 3,20);

        //planner = new GOAPPlanner();
        //planner.replan();

    }

    private void testStackForShockGun()
    {
    BotLogic.getInstance().writeToLog_HackCosIMNoob("start actorSystem");
        BotLogic.getInstance().writeToLog_HackCosIMNoob("plan length" + bb.currentPlan.size());
        if (bb.currentPlan.isEmpty()) {
            
            bb.currentPlan.push(action_schock);
            
            bb.currentPlan.push(action_schockAmmoFinding);
            bb.currentPlan.push(action_randomWalk);
        }

        Actions.Action.ActionResult result = bb.currentPlan.peek().executeAction();

        BotLogic.getInstance().writeToLog_HackCosIMNoob("action executed");

        if (result == Actions.Action.ActionResult.Success) {
            bb.currentPlan.pop();
            BotLogic.getInstance().writeToLog_HackCosIMNoob("plan length" + bb.currentPlan.size());
        }
        if (result == Actions.Action.ActionResult.Failed) {
            bb.currentPlan.pop();
        }
    }
    
    public void update() {
        
        //testStackForShockGun();
        /*
         * if(bb.currentPlan.isEmpty()) {
         * BotLogic.getInstance().writeToLog_HackCosIMNoob("34254325");
         * planner.replan();
         * BotLogic.getInstance().writeToLog_HackCosIMNoob("8986786"); }
         * Action.ActionResult result =
         * bb.currentPlan.firstElement().executeAction();
         *
         * if (result == Action.ActionResult.Success) bb.currentPlan.pop(); if
         * (result == Action.ActionResult.Failed) planner.replan();;
         */


        /*
         *
         * if (action_followVisiblePlayer.executeAction() ==
         * Action.ActionResult.Running) {
         * action_suppressionFire.executeAction(); } else if (
         * !(action_gotoammunition.executeAction() ==
         * Action.ActionResult.Success)) { action_randomWalk.executeAction(); }
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
