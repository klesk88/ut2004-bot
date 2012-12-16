/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import com.fmt.UT2004Bot.WorldState.TruthStates;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author Ado
 */
public class Action_GrenadeThrowing implements Action {

    //use this for the timer
  private float confidence = 0.2f; 
  
  
  
    boolean hasChangedToAssaultRifle = false;

    
      
    public Action_GrenadeThrowing() {
        ActionManager.getInstance().addAction(this);
    }
    
   @Override
    public float getConfidence() {
       // return 1;
       

//       if(!BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.FLAK_CANNON) && !BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.ROCKET_LAUNCHER) && !BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.LIGHTNING_GUN) 
//               && !BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.MINIGUN) &&  !BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE)  )
//       {
//           return 1;
//       }
//       
//         if(!BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.FLAK_CANNON_AMMO) && !BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ROCKET_LAUNCHER_AMMO) && !BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.LIGHTNING_GUN_AMMO) 
//               && !BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.MINIGUN_AMMO) &&  !BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.SHOCK_RIFLE_AMMO) && BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_AMMO))
//       {
//           return 1;
//       }
         
        return confidence;
    }
   
    @Override
    public void setConfidence(float confidence_value) {
         confidence = confidence_value;
    }
    
    private TypeOfAction type_of_action = TypeOfAction.GrenadeThrowing;
    
    @Override
    public TypeOfAction getTypeOfAction() {
         return type_of_action;
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
    public TruthStates[] getPreConditionArray() {
                                TruthStates[] preConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = TruthStates.Uninstantiated;
        }

        preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;
        preConditionArray[WorldState.Symbols.HasGunAmmunition.ordinal()] = TruthStates.True;
        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {

        // fail if no weapon is available
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_AMMO)
                ) || !BlackBoard.getInstance().player_visible ) {
            BlackBoard.getInstance().follow_player = false;
            //BotLogic.getInstance().writeToLog_HackCosIMNoob("Grenade throwing failure - no ammo");
            BotLogic.getInstance().getShoot().stopShooting();
            return ActionResult.Failed;
        }

        if (BotLogic.getInstance().getWeaponry().getCurrentWeapon().getType() == ItemType.ASSAULT_RIFLE) {
            hasChangedToAssaultRifle = true;
        } else {
            hasChangedToAssaultRifle = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("changing to assault rifle");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.ASSAULT_RIFLE);
        }

         if(BlackBoard.getInstance().player==null)
        {
              BlackBoard.getInstance().follow_player = false;
              
            BotLogic.getInstance().getShoot().stopShooting();
          
            
             BlackBoard.getInstance().perform_taunt = true;
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
     
        if( BotLogic.getInstance().getPlayers().canSeePlayers())
            {
                 // BotLogic.getInstance().getPathExecutor().setFocus(BlackBoard.getInstance().player.getLocation());
                   
                 BotLogic.getInstance().getShoot().shootPrimary( BlackBoard.getInstance().predictLocationForWeapon(
                         BlackBoard.WeaponsUsed.ASSAULT_RIFLE_Prim));
            }
        return ActionResult.Running;
    }

  
}
