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
public class Action_ShockGunNuke implements Action {

    //use this for the timer
    double timeStamp_EnergyBallShootBeShot = 0;
    double time_estimatedTimeForShooting = 0.6;
    boolean waitingToShootPrimary = false;
    Location secondaryWasShootAt;
    boolean hasChangedToShockRifle = false;

    public Action_ShockGunNuke() {
        ActionManager.getInstance().addAction(this);
    }

    @Override
    public boolean arePreConditionsMet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] GetPostCondtionsArray() {
        TruthStates[] postConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < postConditionArray.length; i++) {
            postConditionArray[i] = TruthStates.Uninstantiated;
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
        for (int i = 0; i < preConditionArray.length; i++) {
            preConditionArray[i] = TruthStates.Uninstantiated;
        }

        preConditionArray[WorldState.Symbols.ShockGunAmmunition.ordinal()] = TruthStates.True;
        preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;

        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {
        
   
        // fail if no weapon is available
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.SHOCK_RIFLE_AMMO)
                && BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE))) {
           
            waitingToShootPrimary = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke failure");
            BlackBoard.getInstance().follow_player = false;
            return ActionResult.Failed;
        }

        if (BotLogic.getInstance().getWeaponry().getCurrentWeapon().getType() == ItemType.SHOCK_RIFLE) {
            hasChangedToShockRifle = true;
        } else {
            hasChangedToShockRifle = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("changing to shock rifle");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.SHOCK_RIFLE);
        }

        if ((!waitingToShootPrimary)
                && hasChangedToShockRifle
                && BlackBoard.getInstance().predictionIsReady_Tilman) {
            //Shoot secondary
            if (BotLogic.getInstance().getInfo().getNearestPlayer() != null) {
                BlackBoard.getInstance().player = BotLogic.getInstance().getInfo().getNearestPlayer();
                secondaryWasShootAt = BlackBoard.getInstance().predictLocationForWeapon(null);
            } //else if (BotLogic.getInstance().getInfo().getNearestVisibleItem() != null) {
            //    secondaryWasShootAt = BotLogic.getInstance().getInfo().getNearestVisibleItem().getLocation();  
            //} 
            else {
             BlackBoard.getInstance().follow_player = false;
                return ActionResult.Failed;
            }

            BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting secondary shock rifle");
            BotLogic.getInstance().getShoot().shootSecondary(secondaryWasShootAt);
            waitingToShootPrimary = true;
            timeStamp_EnergyBallShootBeShot = BotLogic.getInstance().getGame().getTime() + time_estimatedTimeForShooting;

            //BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
            BlackBoard.getInstance().follow_player = true;
            return ActionResult.Running;
        }

        if (waitingToShootPrimary && hasChangedToShockRifle) {
            if (timeStamp_EnergyBallShootBeShot < BotLogic.getInstance().getGame().getTime()) {

                {
                    //BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting primary shock rifle");
                    BotLogic.getInstance().getShoot().shootPrimary(secondaryWasShootAt);

                    BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke success");
                    waitingToShootPrimary = false;
                    timeStamp_EnergyBallShootBeShot = Double.POSITIVE_INFINITY;
                  BlackBoard.getInstance().follow_player = false;
                    return ActionResult.Success;
                }
            }
        }
//        if(BlackBoard.getInstance().player_visible)
//        {
//             BlackBoard.getInstance().targetPos =  BlackBoard.getInstance().player.getLocation();
//        }
         BlackBoard.getInstance().follow_player = true;      
        //BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
        return ActionResult.Running;
    }
}
