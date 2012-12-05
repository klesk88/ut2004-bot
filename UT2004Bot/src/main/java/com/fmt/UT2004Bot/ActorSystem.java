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
    boolean PLANNING_ENABLED = false;
    BlackBoard bb;
    Actions.Action_SuppressionFire action_suppressionFire;
    Actions.Action_GoToAmmunition action_gotoammunition;
    Actions.Action_FollowVisiblePlayer action_followVisiblePlayer;
    Actions.Action_RandomWalk action_randomWalk;
    Actions.Action_ShockGunNuke action_schock;
    Actions.Action_FindShockGunAmmo action_schockAmmoFinding;
    Actions.Action_FindHealth action_findHealth;
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
        action_findHealth = new Actions.Action_FindHealth();

        if (PLANNING_ENABLED) {
            MTC.getInstance().init(0.05f, 3, 20);
            planner = new GOAPPlanner();
            planner.replan();
        } else {
            bb.currentPlan = new Stack<Actions.Action>();
            bb.currentPlan.push(action_gotoammunition);
            bb.currentPlan.push(action_suppressionFire);
            BotLogic.getInstance().writeToLog_HackCosIMNoob(" !!! PLANNING DISABLED !!!");
        }
    }

    /**
     * Write your test stacks here. Will only be called if PLANNER_ENABLED =
     * false;
     */
    private void testStack() {
        bb.currentPlan.push(action_gotoammunition);
        bb.currentPlan.push(action_suppressionFire);
    }

    /**
     * Will execute actions from the current plan and ask for new plans upon
     * failure or plan completion.
     */
    public void update() {

        if (bb.currentPlan.isEmpty()) {
            if (PLANNING_ENABLED) {
                planner.replan();
            } else {
                testStack();
            }
        }

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
