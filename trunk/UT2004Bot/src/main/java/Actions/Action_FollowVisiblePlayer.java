/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.WorldState;
import com.fmt.UT2004Bot.WorldState.TruthStates;

/**
 *
 * @author Ado
 */
public class Action_FollowVisiblePlayer implements Action{

     public void Action_FollowVisiblePlayer() {
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
            
        postConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;
        
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
            

        preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.False;

        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {
        
        if(BotLogic.getInstance().getPlayers().getNearestVisiblePlayer() == null){
            BotLogic.getInstance().writeToLog_HackCosIMNoob("No player visible");
            return ActionResult.Failed;
        }
        else{
            BotLogic.getInstance().writeToLog_HackCosIMNoob("moving to player");
            BlackBoard.getInstance().follow_player = true;
            BlackBoard.getInstance().targetPos = BotLogic.getInstance().getPlayers().getNearestVisiblePlayer().getLocation();
        } 
            
        return ActionResult.Running;
                
    }

   
   


    
}
