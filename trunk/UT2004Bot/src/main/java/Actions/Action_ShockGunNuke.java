/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
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
    double time_estimatedTimeForShooting = 0.5;
    boolean waitingToShootPrimary = false;
    Location secondaryWasShootAt;

    @Override
    public boolean arePreConditionsMet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] GetPostCondtionsArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] getPreConditionArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ActionResult executeAction() {

        if (!waitingToShootPrimary) {
            //Shoot secondary
            if (BotLogic.getInstance().getWeaponry().hasSecondaryWeaponAmmo(ItemType.SHOCK_RIFLE)) {
                BotLogic.getInstance().writeToLog_HackCosIMNoob("changing to shock rifle");
                BotLogic.getInstance().getShoot().changeWeapon(ItemType.SHOCK_RIFLE);

                if (BotLogic.getInstance().getInfo().getNearestPlayer() != null) {
                    BlackBoard.getInstance().player = BotLogic.getInstance().getInfo().getNearestPlayer();
                    secondaryWasShootAt = BlackBoard.getInstance().player.getLocation();
                } else if (BotLogic.getInstance().getInfo().getNearestVisibleItem() != null) {
                    secondaryWasShootAt = BotLogic.getInstance().getInfo().getNearestVisibleItem().getLocation();
                } else {
                    return ActionResult.Failed;
                }

                BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting secondary shock rifle");
                BotLogic.getInstance().getShoot().shootSecondary(secondaryWasShootAt);
                waitingToShootPrimary = true;
                timeStamp_EnergyBallShootBeShot = BotLogic.getInstance().getGame().getTime();
                
                BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
                return ActionResult.Running;
            } else {
                waitingToShootPrimary = false;
                BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke failure");
                return ActionResult.Failed;
            }

        }

        if (waitingToShootPrimary) {
            if ((timeStamp_EnergyBallShootBeShot + time_estimatedTimeForShooting)
                    < BotLogic.getInstance().getGame().getTime()) {

                if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.SHOCK_RIFLE)) {
                    BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting primary shock rifle");
                    BotLogic.getInstance().getShoot().changeWeapon(ItemType.SHOCK_RIFLE);
                    BotLogic.getInstance().getShoot().shootPrimary(secondaryWasShootAt);

                    BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke success");
                    waitingToShootPrimary = false;
                    return ActionResult.Success;
                } else {
                    waitingToShootPrimary = false;
                    BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke failure");
                    return ActionResult.Failed;
                }
            }
        }
        BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
        return ActionResult.Running;
    }
}