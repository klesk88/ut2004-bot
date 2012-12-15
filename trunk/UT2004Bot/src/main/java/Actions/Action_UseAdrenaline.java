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
public class Action_UseAdrenaline implements Action {
    
    private float confidence = 1f; 
  

     public Action_UseAdrenaline    () {
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
    public void setConfidence(float confidence_value) {
         confidence = confidence_value;
    }
      
      private TypeOfAction type_of_action = TypeOfAction.UseAdrenaline;
    
    @Override
    public TypeOfAction getTypeOfAction() {
         return type_of_action;
    }
     
    @Override
    public WorldState.TruthStates[] GetPostCondtionsArray() {
                        WorldState.TruthStates[] postConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < postConditionArray.length; i++)
        {postConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }
            
        postConditionArray[WorldState.Symbols.PerformAdrenalineAction.ordinal()] = WorldState.TruthStates.True;
        
        return postConditionArray;
    }

   

    @Override
    public WorldState.TruthStates[] getPreConditionArray() {
                                        WorldState.TruthStates[] preConditionArray = new WorldState.TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = WorldState.TruthStates.Uninstantiated;
        }
            
        preConditionArray[WorldState.Symbols.HasAdrenaline.ordinal()] = WorldState.TruthStates.True;
        
        return preConditionArray;
    }

    @Override
    public Action.ActionResult executeAction() {

       if(BotLogic.getInstance().getInfo().getHealth() < 30)
       {
           BotLogic.getInstance().getCombo().performDefensive();
           return Action.ActionResult.Success;
       }
       
       if(BlackBoard.getInstance().player_visible)
       {
           BotLogic.getInstance().getCombo().performBerserk();
           return Action.ActionResult.Success;
       }
       
     
        BotLogic.getInstance().getCombo().performSpeed();
       return Action.ActionResult.Success;
    }

}
