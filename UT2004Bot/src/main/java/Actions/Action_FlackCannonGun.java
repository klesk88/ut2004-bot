/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Map;



/**
 *
 * @author klesk
 */
public class Action_FlackCannonGun implements Action{
    
    private float confidence = 0.7f; 
    
     public Action_FlackCannonGun() {
      Map<UnrealId, Item> weapon = BotLogic.getInstance().getItems().getAllItems(ItemType.Group.FLAK_CANNON);
      if(weapon.size()!=0)
      {
        ActionManager.getInstance().addAction(this);
      }
    }
    
    @Override
    public float getConfidence() {
        
//        //if i have this weapon, but not the weapons with more confidence for kill player imporve this confidence
        if(  !BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.ROCKET_LAUNCHER) && !BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE) 
               && BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.FLAK_CANNON) )
       {
          return 1;
       } 
          
        return confidence;
    }

    @Override
    public WorldState.TruthStates[] GetPostCondtionsArray() {
                WorldState.TruthStates[] postConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < postConditionArray.length; i++)
        {postConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }
            
        postConditionArray[WorldState.Symbols.IsTargetDead.ordinal()] = WorldState.TruthStates.True;
        
        return postConditionArray;
    }

   

    @Override
    public WorldState.TruthStates[] getPreConditionArray() {
                                WorldState.TruthStates[] preConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }

        preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = WorldState.TruthStates.True;
        preConditionArray[WorldState.Symbols.HasFlackAmmo.ordinal()] = WorldState.TruthStates.True;
       
        return preConditionArray;
    }

    @Override
    public Action.ActionResult executeAction() {

        // fail if no weapon is available
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.FLAK_CANNON_AMMO)
                ) || !BlackBoard.getInstance().player_visible ) {
            BlackBoard.getInstance().follow_player = false;
             BotLogic.getInstance().getShoot().stopShooting();
            //BotLogic.getInstance().writeToLog_HackCosIMNoob("Grenade throwing failure - no ammo");
            return Action.ActionResult.Failed;
        }

        if (BotLogic.getInstance().getWeaponry().getCurrentWeapon().getType() == ItemType.FLAK_CANNON) {
           
        } else {
          
           // BotLogic.getInstance().writeToLog_HackCosIMNoob("changing to rocket luncher");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.FLAK_CANNON);
        }
        

        if(BlackBoard.getInstance().player==null)
        {
              BlackBoard.getInstance().follow_player = false;
            BotLogic.getInstance().getShoot().stopShooting();
             return Action.ActionResult.Success;
        }
//        if (hasChangedToAssaultRifle
//                && BlackBoard.getInstance().predictionIsReady_Tilman) {
//
//            //Shoot secondary
//            if (BotLogic.getInstance().getInfo().getNearestPlayer() != null) {
//                
//                //BlackBoard.getInstance().player = BotLogic.getInstance().getInfo().getNearestPlayer();
//                //BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting grenade assault rifle");
//                //BotLogic.getInstance().getShoot().shootSecondary(BlackBoard.getInstance().player.getLocation());
//                BotLogic.getInstance().getShoot().shootSecondaryCharged(
//                        BlackBoard.getInstance().predictLocationForWeapon(null),
//                        0);
//                return ActionResult.Success;
//            
//            } else {
//                
//                BotLogic.getInstance().writeToLog_HackCosIMNoob("Grenade throwing failure - no player");
//                BlackBoard.getInstance().follow_player = false;
//                return ActionResult.Failed;
//            }
//        }
        //BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
//        BotLogic.getInstance().getShoot().shootSecondaryCharged(
//                        BlackBoard.getInstance().predictLocationForWeapon(null),
//                        0);
        BlackBoard.getInstance().follow_player = true;
        if(BotLogic.getInstance().getPlayers().canSeePlayers())
            {
               // BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
          BotLogic.getInstance().getShoot().shootPrimary( BlackBoard.getInstance().predictLocationForWeapon(null));
            }
        return Action.ActionResult.Running;
    }
    
}
