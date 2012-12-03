/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fmt.UT2004Bot;

import com.fmt.UT2004Bot.WorldState.TruthStates;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * Fires depending on ammo in the order Assault, Minigun, Flak
 * 
 * @author Tilman
 */
public class Action_SuppressionFire implements Action{

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
        
        //log.info("Decision is: ENGAGE");
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.ASSAULT_RIFLE))
        {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting because there is ammo");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.ASSAULT_RIFLE);
            BotLogic.getInstance().getShoot().shoot();
            return ActionResult.Success;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.MINIGUN))
        {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting because there is ammo");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.MINIGUN);
            BotLogic.getInstance().getShoot().shoot();
            return ActionResult.Success;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.FLAK_CANNON))
        {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting because there is ammo");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.FLAK_CANNON);
            BotLogic.getInstance().getShoot().shoot();
            return ActionResult.Success;
        }

        return ActionResult.Failed;
    }
    
}
