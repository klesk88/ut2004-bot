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
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.*;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import java.util.*;
/**
 *
 * @author Ado
 */
public class Action_GoToAmmunition implements Action {

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
            
        postConditionArray[WorldState.Symbols.HasSuppressionAmmunition.ordinal()] = TruthStates.True;
        
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
            
        preConditionArray[WorldState.Symbols.HasSuppressionAmmunition.ordinal()] = TruthStates.True;
        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {
        
        BlackBoard bb = BlackBoard.getInstance();
        
        //bb.targetPos = null;
                
        Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(bb.mostDesiredAmmunition[0]).keySet();
        
        int index = 0;
        
        // CAREFUL I(i) start(s) with one, I am a bad boy!!!
        for (int i = 1; i < bb.mostDesiredAmmunition.length; i++)
        {
            if (itemSet.isEmpty())
            {
                index = i;
                itemSet = BotLogic.getInstance().getItems().getAllItems(bb.mostDesiredAmmunition[i]).keySet();
            }
            else break;
        }
        
        Iterator it = itemSet.iterator();

        while(it.hasNext())
        {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("found ammunition");
            UnrealId value = (UnrealId)it.next();
            bb.targetPos = BotLogic.getInstance().getItems().getAllItems(bb.mostDesiredAmmunition[index]).get(value).getLocation();
            
            //TODO: only for testing ! here we return the first value!!! not the closest one
            return ActionResult.Success;
        }
             
        return ActionResult.Failed;
        
        //BotLogic.getInstance().getItems().isPickable(null);
        
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
