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
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Ado
 */
public class Action_FindHealth implements Action {

        private float confidence = 1f; 
    boolean currentlySearchingHealth = false;

     public Action_FindHealth() {
        ActionManager.getInstance().addAction(this);
    }
     
     @Override
    public float getConfidence() {
        return confidence;
    }

    @Override
    public TruthStates[] GetPostCondtionsArray() {
                        TruthStates[] postConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < postConditionArray.length; i++)
        {postConditionArray[i] = TruthStates.Uninstantiated;
        }
            
        postConditionArray[WorldState.Symbols.HasLowHealth.ordinal()] = TruthStates.False;
        
        return postConditionArray;
    }

   

    @Override
    public TruthStates[] getPreConditionArray() {
                                        TruthStates[] preConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = TruthStates.Uninstantiated;
        }
            
        //preConditionArray[WorldState.Symbols.HasLowHealth.ordinal()] = TruthStates.True;
        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {

        boolean health_dropped = false;

        if (BotLogic.getInstance().getBot().getSelf().getHealth() > 50) {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("health search success");
            return ActionResult.Success;

        } else {
            BlackBoard bb = BlackBoard.getInstance();

            Location attempt = bb.getBestHealthPackLocation();

            //if (attempt != null) {
                bb.targetPos = attempt;
                //BotLogic.getInstance().writeToLog_HackCosIMNoob("health search running");
                return ActionResult.Running;
            //} else {
                //BotLogic.getInstance().writeToLog_HackCosIMNoob("no health pack dropped");
               // return ActionResult.Failed;
            //}
        }
    }

    
   
}
