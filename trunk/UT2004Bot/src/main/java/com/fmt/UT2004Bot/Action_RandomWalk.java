/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import com.fmt.UT2004Bot.WorldState.TruthStates;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 *
 * @author Ado
 */
public class Action_RandomWalk implements Action{

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
        BlackBoard.getInstance().targetPos = getRandomNavPoint().getLocation();
        return ActionResult.Success;
    }
    
        /**
     * Randomly picks some navigation point to head to.
     *
     * @return randomly choosed navpoint
     */
    private NavPoint getRandomNavPoint() {
        BotLogic.getInstance().getLog().info("Picking new target navpoint.");

        // choose one feasible navpoint (== not belonging to tabooNavPoints) randomly
        NavPoint chosen 
                = MyCollections.getRandomFiltered(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values(), 
                MovementLogic.getInstance().tabooNavPoints);

        if (chosen != null) {
            return chosen;
        }

        BotLogic.getInstance().getLog().warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");

        // ok, all navpoints have been visited probably, try to pick one at random
        return MyCollections.getRandom(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values());
    }
    
}
