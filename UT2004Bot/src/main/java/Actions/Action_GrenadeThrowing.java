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
  
  
  
  
    boolean hasChangedToAssaultRifle = false;

    
      
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
        if (!(BotLogic.getInstance().getWeaponry().hasAmmo(ItemType.ASSAULT_RIFLE_GRENADE)
                )) {

            //BotLogic.getInstance().writeToLog_HackCosIMNoob("Grenade throwing failure - no ammo");
            return ActionResult.Failed;
        }

        if (BotLogic.getInstance().getWeaponry().getCurrentWeapon().getType() == ItemType.ASSAULT_RIFLE) {
            hasChangedToAssaultRifle = true;
        } else {
            hasChangedToAssaultRifle = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("changing to assault rifle");
            BotLogic.getInstance().getShoot().changeWeapon(ItemType.ASSAULT_RIFLE);
        }


        if (hasChangedToAssaultRifle
                && BlackBoard.getInstance().predictionIsReady_Tilman) {

            //Shoot secondary
            if (BotLogic.getInstance().getInfo().getNearestPlayer() != null) {
                
                BlackBoard.getInstance().player = BotLogic.getInstance().getInfo().getNearestPlayer();
                //BotLogic.getInstance().writeToLog_HackCosIMNoob("shooting grenade assault rifle");
                //BotLogic.getInstance().getShoot().shootSecondary(BlackBoard.getInstance().player.getLocation());
                BotLogic.getInstance().getShoot().shootSecondaryCharged(
                        BlackBoard.getInstance().predictLocationForWeapon(null),
                        0);
                return ActionResult.Success;
            
            } else {
                
                BotLogic.getInstance().writeToLog_HackCosIMNoob("Grenade throwing failure - no player");
                return ActionResult.Failed;
            }
        }
        //BotLogic.getInstance().writeToLog_HackCosIMNoob("ShockGunNuke running");
        return ActionResult.Running;
    }

  
}
