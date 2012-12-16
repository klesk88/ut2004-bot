/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.BlackBoard;
import com.fmt.UT2004Bot.BotLogic;
import com.fmt.UT2004Bot.MovementLogic;
import com.fmt.UT2004Bot.WorldState;
import com.fmt.UT2004Bot.WorldState.TruthStates;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 *
 * @author Ado
 */
public class Action_RandomWalk implements Action{
private float confidence = 1f; 
    boolean newRun = true;
    
    
    public Action_RandomWalk() {
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
            
        postConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.True;
        
        return postConditionArray;
    }

    
 @Override
    public void setConfidence(float confidence_value) {
         confidence = confidence_value;
    }
 
 
 private TypeOfAction type_of_action = TypeOfAction.RandomWak;
    
    @Override
    public TypeOfAction getTypeOfAction() {
         return type_of_action;
    }
    
        @Override
    public TruthStates[] getPreConditionArray() {
                TruthStates[] preConditionArray = new TruthStates[WorldState.Symbols.values().length];
        for (int i = 0; i < preConditionArray.length; i++)
        {preConditionArray[i] = TruthStates.Uninstantiated;
        }
            
       // preConditionArray[WorldState.Symbols.PlayerIsVisible.ordinal()] = TruthStates.False;
        //preConditionArray[WorldState.Symbols.HasLowHealth.ordinal()] = TruthStates.False;
        
        return preConditionArray;
    }

    @Override
    public ActionResult executeAction() {
          //BlackBoard.getInstance().follow_player = false;
        BlackBoard bb = BlackBoard.getInstance();
        
//        if (newRun){
//            bb.targetPos = getRandomNavPoint().getLocation();
//            BotLogic.getInstance().getBody().getLocomotion().setRun();
//            newRun = false;
//        }
        //@Michele. fix navigation system
//        NavPoint temp_navpoint = MovementLogic.getInstance().handleNavPointNavigation();
//        
//        if(MovementLogic.getInstance().handleNavPointNavigation() != null)
//        {
//             bb.targetPos = temp_navpoint.getLocation();
//        }
        
        if (/*( BotLogic.getInstance().getBot().getVelocity().isZero()) &&*/ bb.player_visible){
            newRun = true;
             bb.randomWalk=false;
            BotLogic.getInstance().writeToLog_HackCosIMNoob("RandomWalk success");
            return ActionResult.Success;
        } 
        
        bb.randomWalk=true;
            
        BotLogic.getInstance().writeToLog_HackCosIMNoob("RandomWalk Running");
        return ActionResult.Running;
    }
        
    
    
        /**
     * Randomly picks some navigation point to head to.
     *
     * @return randomly choosed navpoint
     */
    private NavPoint getRandomNavPoint() {
        //BotLogic.getInstance().getLog().info("Picking new target navpoint.");

        // choose one feasible navpoint (== not belonging to tabooNavPoints) randomly
        NavPoint chosen 
                = MyCollections.getRandomFiltered(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values(), 
                MovementLogic.getInstance().tabooNavPoints);

        if (chosen != null) {
            return chosen;
        }

        //BotLogic.getInstance().getLog().warning("All navpoints are tabooized at this moment, choosing navpoint randomly!");

        // ok, all navpoints have been visited probably, try to pick one at random
        return MyCollections.getRandom(BotLogic.getInstance().getWorld().getAll(NavPoint.class).values());
    }

   


    
}
