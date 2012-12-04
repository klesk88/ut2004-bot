/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Actions;

import com.fmt.UT2004Bot.WorldState;
import java.util.logging.Level;

/**
 *
 * @author Tilman, Klesk
 */
public interface Action 
{
    public enum ActionResult {Success, Failed, Running}
    
    //pre - post conditionss
    
    /**
     * check if the pre-conditions are met. 
     * This can be done procedurally or simply by asking the worldstate about the truth-value of a symbol
     * @return true if all pre-conditions are met, false otherwise.
     */
    public boolean arePreConditionsMet();
   
    /**
     * Apply post-conditions to the (copy of the) world state
     */
    public WorldState.TruthStates[] GetPostCondtionsArray();
    
    //public void UndoPostConditions();
    
    /**
     * update that check the state of the current action
     */
    public void update();
    
    /**
     * Apply the pre conditions to the goal state of the world for add them in the search
     * @param goal_state previous goal_state of the world
     * @return new goal_state of the world
     */
    // public WorldState.TruthStates[] applyPreConditions(WorldState.TruthStates[] goal_state);
    
    public WorldState.TruthStates[] getPreConditionArray();
    
    public ActionResult executeAction();
    
}
