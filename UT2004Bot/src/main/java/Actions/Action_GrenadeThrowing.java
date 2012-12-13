/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import com.fmt.UT2004Bot.WorldState.TruthStates;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;

/**
 *
 * @author Ado
 */
public class Action_GrenadeThrowing implements Action {

    //use this for the timer
    double timeStamp_EnergyBallShootBeShot = 0;
    double time_estimatedTimeForShooting = 0.5;
    boolean waitingToShootPrimary = false;
    Location secondaryWasShootAt;
    boolean hasChangedToShockRifle = false;

    
      
    public Action_GrenadeThrowing() {
        ActionManager.getInstance().addAction(this);
    }
    
    @Override
    public boolean arePreConditionsMet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] GetPostCondtionsArray() {
                TruthStates[] postConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < postConditionArray.length; i++)
        {postConditionArray[i] = TruthStates.Uninstantiated;
        }
            
        postConditionArray[WorldState.Symbols.IsTargetDead.ordinal()] = TruthStates.True;
        
        return postConditionArray;
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] getPreConditionArray() {
                                TruthStates[] preConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = TruthStates.Uninstantiated;
        }

        preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;
        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {

        // fail if no weapon is available
        if (!(BotLogic.getInstance().getWeaponry().hasAmmoForWeapon(ItemType.ASSAULT_RIFLE_GRENADE)
                )) {

            waitingToShootPrimary = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke failure");
            return ActionResult.Failed;
        }

        if (BotLogic.getInstance().getWeaponry().getCurrentWeapon().getType() == ItemType.ASSAULT_RIFLE_GRENADE) {
            hasChangedToShockRifle = true;
        } else {
            hasChangedToShockRifle = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("changing to shock rifle");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.ASSAULT_RIFLE_GRENADE);
        }

        if ((!waitingToShootPrimary) && hasChangedToShockRifle) {
            //Shoot secondary
            if (BotLogic.getInstance().getInfo().getNearestPlayer() != null) {
                BlackBoard.getInstance().player = BotLogic.getInstance().getInfo().getNearestPlayer();
                secondaryWasShootAt = BlackBoard.getInstance().player.getLocation();
            } 
            //else if (BotLogic.getInstance().getInfo().getNearestVisibleItem() != null) {
            //    secondaryWasShootAt = BotLogic.getInstance().getInfo().getNearestVisibleItem().getLocation();  
        //} 
        else {
                return ActionResult.Failed;
            }

            BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting grenade assault rifle");
            BotLogic.getInstance().getShoot().shootSecondary(secondaryWasShootAt);
            waitingToShootPrimary = true;
            timeStamp_EnergyBallShootBeShot = BotLogic.getInstance().getGame().getTime();

            //BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
            return ActionResult.Running;
        }

        if (waitingToShootPrimary && hasChangedToShockRifle) {
            if ((timeStamp_EnergyBallShootBeShot + time_estimatedTimeForShooting)
                    < BotLogic.getInstance().getGame().getTime()) {

                {
                    //BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting primary shock rifle");
                    BotLogic.getInstance().getShoot().shootSecondary(secondaryWasShootAt);

                    BotLogic.getInstance().writeToLog_HackCosIMNoob("Grenade success");
                    waitingToShootPrimary = false;
                    return ActionResult.Success;
                }
            }
        }

        //BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
        return ActionResult.Running;
    }

  
}
