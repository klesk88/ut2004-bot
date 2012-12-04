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
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Ado
 */
public class Action_FindShockGunAmmo implements Action {

    boolean currentlySearchingAmmo = false;
    
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
        
        if (!currentlySearchingAmmo)
        {
        BlackBoard bb = BlackBoard.getInstance();
        
        //bb.targetPos = null;
                
        Set<UnrealId> itemSet = BotLogic.getInstance().getItems().getAllItems(ItemType.SHOCK_RIFLE_AMMO).keySet();
        
        Iterator it = itemSet.iterator();

        while(it.hasNext())
        {
            BotLogic.getInstance().writeToLog_HackCosIMNoob("found shock ammunition");
            UnrealId value = (UnrealId)it.next();
            bb.targetPos = BotLogic.getInstance().getItems().getAllItems(ItemType.SHOCK_RIFLE_AMMO).get(value).getLocation();
            
            //TODO: only for testing ! here we return the first value!!! not the closest one
            currentlySearchingAmmo = true;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc running");
            return ActionResult.Running;
        }
        }
        else if ( BotLogic.getInstance().getWeaponry().getAmmo(ItemType.SHOCK_RIFLE_AMMO) > 5)
        {
            currentlySearchingAmmo = false; 
            BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc success");
                return ActionResult.Success;
                   
        }
        BotLogic.getInstance().writeToLog_HackCosIMNoob("Shock ammo searhc failure");
        currentlySearchingAmmo = false;     
        return ActionResult.Failed;
    }
    
}