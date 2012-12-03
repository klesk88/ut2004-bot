/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import com.fmt.UT2004Bot.WorldState.TruthStates;

/**
 *
 * @author Ado
 */
public class Action_FollowVisiblePlayer implements Action{

    @Override
    public boolean arePreConditionsMet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] ApplyPostCondtions(TruthStates[] worls_state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] applyPreConditions(TruthStates[] goal_state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ActionResult executeAction() {
        
        if(BotLogic.getInstance().getPlayers().getNearestVisiblePlayer() == null){
            BotLogic.getInstance().writeToLog_HackCosIMNoob("No player visible");
            return ActionResult.Failed;
        }
        else{
            BotLogic.getInstance().writeToLog_HackCosIMNoob("moving to player");
            BlackBoard.getInstance().follow_player = true;
            BlackBoard.getInstance().targetPos = BotLogic.getInstance().getPlayers().getNearestVisiblePlayer().getLocation();
        } 
            
        return ActionResult.Running;
                
    }
    
}
