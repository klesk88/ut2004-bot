/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
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
public class Action_SearchAdrenaline implements Action{
    
       private float confidence = 1f; 
  

     public Action_SearchAdrenaline() {
          Map<UnrealId, Item> weapon = BotLogic.getInstance().getItems().getAllItems(ItemType.Group.ADRENALINE);
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
            
        postConditionArray[WorldState.Symbols.HasAdrenaline.ordinal()] = WorldState.TruthStates.True;
        
        return postConditionArray;
    }

   

    @Override
    public WorldState.TruthStates[] getPreConditionArray() {
                                        WorldState.TruthStates[] preConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }
            
        //preConditionArray[WorldState.Symbols.HasLowHealth.ordinal()] = TruthStates.True;
        
        return preConditionArray;
    }

    @Override
    public Action.ActionResult executeAction() {

       BlackBoard bb = BlackBoard.getInstance();

            Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(ItemType.ADRENALINE_PACK).keySet();

            Iterator it = itemSet.iterator();

            while (it.hasNext()) {
                UnrealId value = (UnrealId) it.next();
                bb.targetPos = BotLogic.getInstance().getItems().getAllItems(ItemType.ADRENALINE_PACK).get(value).getLocation();
                //BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock RIFLE search running");
                return Action.ActionResult.Running;
            }
            
            if(BotLogic.getInstance().getInfo().isAdrenalineSufficient())
            {
                return Action.ActionResult.Success;
            }
            
            return Action.ActionResult.Failed;
    }

    
}
