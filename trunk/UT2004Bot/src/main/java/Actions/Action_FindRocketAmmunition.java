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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author klesk
 */
public class Action_FindRocketAmmunition implements Action {
     private float confidence = 1f; 
     boolean currentlySearchingAmmo = false;
     
    public Action_FindRocketAmmunition() {
       Map<UnrealId, Item> weapon = BotLogic.getInstance().getItems().getAllItems(ItemType.Group.ROCKET_LAUNCHER);
      if(weapon.size()!=0)
      {
        ActionManager.getInstance().addAction(this);
      }
    }
    
  
 @Override
    public float getConfidence() {
        return confidence;
    }
    @Override
    public WorldState.TruthStates[] GetPostCondtionsArray() {
        WorldState.TruthStates[] postConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < postConditionArray.length; i++)
        {postConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }
            
        postConditionArray[WorldState.Symbols.HasRocketAmmunition.ordinal()] = WorldState.TruthStates.True;
        
        return postConditionArray;
    }

  

    @Override
    public WorldState.TruthStates[] getPreConditionArray() {
                        WorldState.TruthStates[] preConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }
            
        //preConditionArray[WorldState.Symbols.ShockGunAmmunition.ordinal()] = TruthStates.False;
        preConditionArray[WorldState.Symbols.HasLowHealth.ordinal()] = WorldState.TruthStates.False;
        
        return preConditionArray;
    }

    @Override
    public Action.ActionResult executeAction() {

        if(! BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.ROCKET_LAUNCHER))
        {
            BlackBoard bb = BlackBoard.getInstance();

            Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(ItemType.ROCKET_LAUNCHER).keySet();

            Iterator it = itemSet.iterator();

            while (it.hasNext()) {
                UnrealId value = (UnrealId) it.next();
                bb.targetPos = BotLogic.getInstance().getItems().getAllItems(ItemType.ROCKET_LAUNCHER).get(value).getLocation();
                //BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock RIFLE search running");
                return Action.ActionResult.Running;
            }
        }
        
        if (!currentlySearchingAmmo) {
            BlackBoard bb = BlackBoard.getInstance();

            //bb.targetPos = null;

            Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(ItemType.ROCKET_LAUNCHER_AMMO).keySet();

            Iterator it = itemSet.iterator();

            while (it.hasNext()) {
               // BotLogic.getInstance().writeToLog_HackCosIMNoob("found shock ammunition");
                UnrealId value = (UnrealId) it.next();
                bb.targetPos = BotLogic.getInstance().getItems().getAllItems(ItemType.ROCKET_LAUNCHER_AMMO).get(value).getLocation();

                //TODO: only for testing ! here we return the first value!!! not the closest one
                currentlySearchingAmmo = true;
                //BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc running");
                return Action.ActionResult.Running;
            }
        } else if (BotLogic.getInstance().getWeaponry().getAmmo(ItemType.ROCKET_LAUNCHER_AMMO) > 5) {
            currentlySearchingAmmo = false;
            //BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc success");
            return Action.ActionResult.Success;

        }
       // BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc failure");
        currentlySearchingAmmo = false;
        return Action.ActionResult.Failed;
    }

    
}
