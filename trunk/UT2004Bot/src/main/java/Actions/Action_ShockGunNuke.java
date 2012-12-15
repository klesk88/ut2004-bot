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
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Map;

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
     private float confidence = 0.9f; 
     
    public Action_ShockGunNuke() {
       Map<UnrealId, Item> weapon = BotLogic.getInstance().getItems().getAllItems(ItemType.Group.SHOCK_RIFLE);
      if(weapon.size()!=0)
      {
        ActionManager.getInstance().addAction(this);
      }
    }

   @Override
    public float getConfidence() {
      // return 1;
         //if i don't have this weapon right now
//          if(BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.FLAK_CANNON) || BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.ROCKET_LAUNCHER) || BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.LIGHTNING_GUN) 
//               || BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.MINIGUN) ||  !BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_AMMO)  )
//       {
//          return 0;
//      }
        return confidence;
    }
   
    @Override
    public void setConfidence(float confidence_value) {
         confidence = confidence_value;
    }
    
    private TypeOfAction type_of_action = TypeOfAction.ShockGunNuke;
    
    @Override
    public TypeOfAction getTypeOfAction() {
         return type_of_action;
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
            BotLogic.getInstance().getShoot().stopShooting();
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
            if (BlackBoard.getInstance().player != null) {
               // BlackBoard.getInstance().player = BotLogic.getInstance().getInfo().getNearestPlayer();
                secondaryWasShootAt = BlackBoard.getInstance().predictLocationForWeapon(null);
            } //else if (BotLogic.getInstance().getInfo().getNearestVisibleItem() != null) {
            //    secondaryWasShootAt = BotLogic.getInstance().getInfo().getNearestVisibleItem().getLocation();  
            //} 
            else {
             BlackBoard.getInstance().follow_player = false;
             BotLogic.getInstance().getShoot().stopShooting();  
             return ActionResult.Failed;
            }
            //Michele: set the focus of the bot to the player
            BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
            BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting secondary shock rifle");
            if(BotLogic.getInstance().getPlayers().canSeePlayers())
            {
               // BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
                BotLogic.getInstance().getShoot().shootSecondary(secondaryWasShootAt);
            }
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
                      if(BotLogic.getInstance().getPlayers().canSeePlayers())
                     {
                          //BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
                         BotLogic.getInstance().getShoot().shootPrimary(secondaryWasShootAt);
                     }

                    BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke success");
                    waitingToShootPrimary = false;
                    timeStamp_EnergyBallShootBeShot = Double.POSITIVE_INFINITY;
                  BlackBoard.getInstance().follow_player = false;
                  BotLogic.getInstance().getShoot().stopShooting(); 
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
