/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import com.fmt.UT2004Bot.WorldState.TruthStates;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Ado
 */
public class Action_FindShockGunAmmo implements Action {
    private float confidence = 1f; 
    boolean currentlySearchingAmmo = false;

    public Action_FindShockGunAmmo() {
       Map<UnrealId, Item> weapon = BotLogic.getInstance().getItems().getAllItems(ItemType.Group.SHOCK_RIFLE);
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
    public void setConfidence(float confidence_value) {
         confidence = confidence_value;
    }
      
      private TypeOfAction type_of_action = TypeOfAction.FindShockgunGunAmmo;
    
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
            
        postConditionArray[WorldState.Symbols.ShockGunAmmunition.ordinal()] = TruthStates.True;
        
        return postConditionArray;
    }

    @Override
    public TruthStates[] getPreConditionArray() {
                        TruthStates[] preConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = TruthStates.Uninstantiated;
        }
            
        //preConditionArray[WorldState.Symbols.ShockGunAmmunition.ordinal()] = TruthStates.False;
        preConditionArray[WorldState.Symbols.HasLowHealth.ordinal()] = TruthStates.False;
        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {

        if(! BotLogic.getInstance().getWeaponry().hasWeapon(ItemType.SHOCK_RIFLE))
        {
            BlackBoard bb = BlackBoard.getInstance();

            Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(ItemType.SHOCK_RIFLE).keySet();

            Iterator it = itemSet.iterator();

            while (it.hasNext()) {
                UnrealId value = (UnrealId) it.next();
                bb.targetPos = BotLogic.getInstance().getItems().getAllItems(ItemType.SHOCK_RIFLE).get(value).getLocation();
                BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock RIFLE search running");
                return ActionResult.Running;
            }
        }
        
        if (!currentlySearchingAmmo) {
            BlackBoard bb = BlackBoard.getInstance();

            //bb.targetPos = null;

            Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(ItemType.SHOCK_RIFLE_AMMO).keySet();

            Iterator it = itemSet.iterator();

            while (it.hasNext()) {
                BotLogic.getInstance().writeToLog_HackCosIMNoob("found shock ammunition");
                UnrealId value = (UnrealId) it.next();
                bb.targetPos = BotLogic.getInstance().getItems().getAllItems(ItemType.SHOCK_RIFLE_AMMO).get(value).getLocation();

                //TODO: only for testing ! here we return the first value!!! not the closest one
                currentlySearchingAmmo = true;
                BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc running");
                return ActionResult.Running;
            }
        } else if (BotLogic.getInstance().getWeaponry().getAmmo(ItemType.SHOCK_RIFLE_AMMO) > 5) {
            currentlySearchingAmmo = false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc success");
            return ActionResult.Success;

        }
        BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc failure");
        currentlySearchingAmmo = false;
        return ActionResult.Failed;
    }

    
    
}
