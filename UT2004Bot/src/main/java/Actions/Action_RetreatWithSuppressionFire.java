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
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * Fires depending on ammo in the order Assault, Minigun, Flak
 *
 * @author Tilman
 */
public class Action_RetreatWithSuppressionFire implements Action {

    
      
    public Action_RetreatWithSuppressionFire() {
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

        preConditionArray[WorldState.Symbols.HasSuppressionAmmunition.ordinal()] = TruthStates.True;
        preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;

        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {

        Player playerEscapeFrom = BotLogic.getInstance().getInfo().getNearestVisiblePlayer();

        if (playerEscapeFrom == null) {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Action_RetreatWithSuppressionFire successful");
            return ActionResult.Success;
        }

        randomDodging(playerEscapeFrom);
        
        setBestHealthPackAsLocation();

        //log.info("Decision is: ENGAGE");
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.ASSAULT_RIFLE)) {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Action_RetreatWithSuppressionFire running");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.ASSAULT_RIFLE);
            BotLogic.getInstance().getShoot().shoot();
            return ActionResult.Running;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.MINIGUN)) {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Action_RetreatWithSuppressionFire running");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.MINIGUN);
            BotLogic.getInstance().getShoot().shoot();
            return ActionResult.Running;
        }
        if (BotLogic.getInstance().getWeaponry().hasPrimaryWeaponAmmo(ItemType.FLAK_CANNON)) {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Action_RetreatWithSuppressionFire running");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.FLAK_CANNON);
            BotLogic.getInstance().getShoot().shoot();

            return ActionResult.Running;
        }

        BotLogic.getInstance().writeToLog_HackCosIMNoob("Action_RetreatWithSuppressionFire failed");
        return ActionResult.Failed;
    }

    private ActionResult setBestHealthPackAsLocation() {
        BlackBoard bb = BlackBoard.getInstance();

        Location attempt = bb.getBestHealthPackLocation();

        if (attempt != null) {
            bb.targetPos = attempt;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("health search running");
            return ActionResult.Running;
        } else {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("no health pack dropped");
            return ActionResult.Failed;
        }
    }
    
    private void randomDodging(Player playerEscapeFrom)
    {
            BotLogic.getInstance().getBody().getLocomotion().turnTo(playerEscapeFrom);
        int avoidanceChoice = (int) (Math.random() * 7);
        if (avoidanceChoice == 1) {
            BotLogic.getInstance().getBody().getLocomotion().jump();
        } else if (avoidanceChoice == 2) {
            BotLogic.getInstance().getBody().getLocomotion().doubleJump();
        } else if (avoidanceChoice == 3) {
            int strafeLength = 100 + ((int) (Math.random() * 250));
            BotLogic.getInstance().getBody().getLocomotion().strafeLeft(strafeLength);
        } else if (avoidanceChoice == 4) {
            int strafeLength = 100 + ((int) (Math.random() * 250));
            BotLogic.getInstance().getBody().getLocomotion().strafeRight(strafeLength);
        }
    }

  
}
