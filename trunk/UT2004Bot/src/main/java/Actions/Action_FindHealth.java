/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
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

    boolean currentlySearchingHealth = false;

     public Action_FindHealth() {
       // ActionManager.getInstance().addAction(this);
    }
     
    @Override
    public boolean arePreConditionsMet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] GetPostCondtionsArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruthStates[] getPreConditionArray() {
        throw new UnsupportedOperationException("Not supported yet.");
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

            if (attempt != null) {
                bb.targetPos = attempt;
                BotLogic.getInstance().writeToLog_HackCosIMNoob("health search running");
                return ActionResult.Running;
            } else {
                BotLogic.getInstance().writeToLog_HackCosIMNoob("no health pack dropped");
                return ActionResult.Failed;
            }
        }
    }

    
   
}
