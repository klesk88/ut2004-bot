/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * Select goals for goap Select target enemies
 *
 * @author Michele, Tilman
 */
public class TargetManager {
    //SINGLETON START

    private static TargetManager instance = null;
    private GOAPPlanner goap = null;
    private BlackBoard bb;

    private TargetManager() {
        // Exists only to defeat instantiation.
        bb = BlackBoard.getInstance();
        goap = GOAPPlanner.getInstance();
    }

    public static TargetManager getInstance() {
        if (instance == null) {
            instance = new TargetManager();
        }
        return instance;
    }
    //SINGLETON END

    public void init() {
    }

    public void update() {
        updateWorldState();
        BotLogic.getInstance().writeToLog_HackCosIMNoob("world state updated");

        //
        

        if((BotLogic.getInstance().getBot().getSelf().getHealth() > 50)
                && (hasSuppressionAmmo() 
                || (!(BotLogic.getInstance().getWeaponry().hasAmmoForWeapon(ItemType.SHOCK_RIFLE)
                && BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE)))))
        {
            WorldState.getInstance().setGoalState(WorldState.GoalStates.KillEnemy);
            return;
        }
        if (!(BotLogic.getInstance().getBot().getSelf().getHealth() > 50))
        {
            WorldState.getInstance().setGoalState(WorldState.GoalStates.Survive);
            return;
        }
        WorldState.getInstance().setGoalState(WorldState.GoalStates.SearchRandomly);
        
        // if goal achieved create new target and replan

        // ask blackboard whether to replan

    }

    private void updateWorldState() {
        
        boolean player_was_visible = bb.player_visible;
        
        if (bb.player_visible) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.PlayerIsVisible, true);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.PlayerIsVisible, false);
        }

        if (hasSuppressionAmmo()) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasSuppressionAmmunition, true);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasSuppressionAmmunition, false);
        }

        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.SHOCK_RIFLE_AMMO)
                && BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE))) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.ShockGunAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.ShockGunAmmunition, true);
        }
        
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_GRENADE)
                )) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasGranadeAmmunition, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasGranadeAmmunition, true);
        }

        if (BotLogic.getInstance().getBot().getSelf().getHealth() > 50) {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLowHealth, false);
        } else {
            WorldState.getInstance().setWSValue(WorldState.Symbols.HasLowHealth, true);
        }
       
//        if (bb.player != null) {
//            if (!BotLogic.getInstance().getGame().isPlayerDeathsKnown(bb.player.getId())) {
//                WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, false);
//            } else if (BotLogic.getInstance().getGame().getPlayerDeaths(bb.player.getId())
//                    > bb.lastKnownDeathValue) {
//                int c = BotLogic.getInstance().getGame().getPlayerDeaths(bb.player.getId());
//                WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, true);
//                bb.lastKnownDeathValue++;
//            }
//        }
        
        if(player_was_visible && !bb.player_visible)
        {
            WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, true);
        }
        else
        {
            WorldState.getInstance().setWSValue(WorldState.Symbols.IsTargetDead, false);
        }

    }

    private boolean hasSuppressionAmmo() {
        boolean result = false;

        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.ASSAULT_RIFLE)) {
            result = true;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.FLAK_CANNON)) {
            result = true;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.MINIGUN)) {
            result = true;
        }

        return result;
    }
}
